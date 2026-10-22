package meme.utils;

import io.github.humbleui.skija.Image;
import meme.core.MemeError;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP tools/image_operations 的底层实现，语义对齐 Rust meme_generator::tools::image_operations。
 * 返回 PNG 或 GIF 字节；inspect 返回图片信息。
 */
public final class ImageOps {

    public record ImageInfo(
            int width,
            int height,
            @com.fasterxml.jackson.annotation.JsonProperty("is_multi_frame") boolean isMultiFrame,
            @com.fasterxml.jackson.annotation.JsonProperty("frame_count") Integer frameCount,
            @com.fasterxml.jackson.annotation.JsonProperty("average_duration") Float averageDuration) {
    }

    private ImageOps() {
    }

    public static ImageInfo inspect(byte[] data) {
        if (GifSupport.isGif(data)) {
            List<GifSupport.GifFrame> frames = GifSupport.readGif(data);
            boolean multi = frames.size() > 1;
            BufferedImage first = frames.get(0).image();
            return new ImageInfo(first.getWidth(), first.getHeight(), multi,
                    multi ? frames.size() : null,
                    multi ? GifSupport.averageDuration(frames) : null);
        }
        Image img = ImageEdit.decode(data);
        return new ImageInfo(img.getWidth(), img.getHeight(), false, null, null);
    }

    // ---------- 单图操作 ----------

    public static byte[] flipHorizontal(byte[] data) {
        return singleFrameOp(data, ImageEdit::flipHorizontal);
    }

    public static byte[] flipVertical(byte[] data) {
        return singleFrameOp(data, ImageEdit::flipVertical);
    }

    public static byte[] rotate(byte[] data, Float degrees) {
        float deg = degrees == null ? 90f : degrees;
        return singleFrameOp(data, img -> ImageEdit.rotate(img, deg));
    }

    public static byte[] resize(byte[] data, Integer width, Integer height) {
        return singleFrameOp(data, img -> {
            if (width == null && height == null) {
                return img;
            }
            if (width == null) {
                return ImageEdit.resizeHeight(img, height);
            }
            if (height == null) {
                return ImageEdit.resizeWidth(img, width);
            }
            return ImageEdit.resizeExact(img, width, height);
        });
    }

    public static byte[] crop(byte[] data, Integer left, Integer top, Integer right, Integer bottom) {
        return singleFrameOp(data, img -> {
            int w = img.getWidth();
            int h = img.getHeight();
            int l = clamp(left == null ? 0 : left, 0, w);
            int t = clamp(top == null ? 0 : top, 0, h);
            int r = clamp(right == null ? w : right, 0, w);
            int b = clamp(bottom == null ? h : bottom, 0, h);
            return ImageEdit.crop(img, l, t, r, b);
        });
    }

    public static byte[] grayscale(byte[] data) {
        return singleFrameOp(data, ImageEdit::grayscale);
    }

    public static byte[] invert(byte[] data) {
        return singleFrameOp(data, ImageEdit::invert);
    }

    // ---------- 合并 ----------

    public static byte[] mergeHorizontal(List<byte[]> images) {
        return ImageEdit.encodePng(ImageEdit.mergeHorizontal(decodeAll(images)));
    }

    public static byte[] mergeVertical(List<byte[]> images) {
        return ImageEdit.encodePng(ImageEdit.mergeVertical(decodeAll(images)));
    }

    // ---------- GIF 操作 ----------

    /** 拆帧：返回每帧 PNG。 */
    public static List<byte[]> gifSplit(byte[] data) {
        List<GifSupport.GifFrame> frames = GifSupport.readGif(data);
        List<byte[]> out = new ArrayList<>();
        for (GifSupport.GifFrame f : frames) {
            Image img = ImageEdit.fromBufferedImage(f.image());
            out.add(ImageEdit.encodePng(img));
        }
        return out;
    }

    /** 合成 GIF：多个图片（GIF 取全部帧），统一帧时长。 */
    public static byte[] gifMerge(List<byte[]> images, Float duration) {
        float dur = duration == null ? 0.1f : duration;
        List<BufferedImage> frames = new ArrayList<>();
        for (byte[] data : images) {
            if (GifSupport.isGif(data)) {
                for (GifSupport.GifFrame f : GifSupport.readGif(data)) {
                    frames.add(f.image());
                }
            } else {
                frames.add(ImageEdit.toBufferedImage(ImageEdit.decode(data)));
            }
        }
        return GifSupport.writeGifUniform(frames, dur);
    }

    /** 倒放。 */
    public static byte[] gifReverse(byte[] data) {
        List<GifSupport.GifFrame> frames = GifSupport.readGif(data);
        List<BufferedImage> reversed = new ArrayList<>();
        int[] delays = new int[frames.size()];
        for (int i = 0; i < frames.size(); i++) {
            GifSupport.GifFrame f = frames.get(frames.size() - 1 - i);
            reversed.add(f.image());
            delays[i] = f.delayMs();
        }
        return GifSupport.writeGif(reversed, delays);
    }

    /** 修改帧时长。 */
    public static byte[] gifChangeDuration(byte[] data, float duration) {
        List<GifSupport.GifFrame> frames = GifSupport.readGif(data);
        List<BufferedImage> imgs = new ArrayList<>();
        for (GifSupport.GifFrame f : frames) {
            imgs.add(f.image());
        }
        return GifSupport.writeGifUniform(imgs, duration);
    }

    // ---------- helpers ----------

    private static byte[] singleFrameOp(byte[] data, java.util.function.Function<Image, Image> func) {
        if (GifSupport.isGif(data)) {
            List<GifSupport.GifFrame> frames = GifSupport.readGif(data);
            if (frames.size() > 1) {
                // 逐帧应用后重新合成 GIF
                List<BufferedImage> out = new ArrayList<>();
                int[] delays = new int[frames.size()];
                for (int i = 0; i < frames.size(); i++) {
                    GifSupport.GifFrame f = frames.get(i);
                    Image processed = func.apply(ImageEdit.fromBufferedImage(f.image()));
                    out.add(ImageEdit.toBufferedImage(processed));
                    delays[i] = f.delayMs();
                }
                return GifSupport.writeGif(out, delays);
            }
            Image img = ImageEdit.fromBufferedImage(frames.get(0).image());
            return ImageEdit.encodePng(func.apply(img));
        }
        Image img = ImageEdit.decode(data);
        return ImageEdit.encodePng(func.apply(img));
    }

    private static List<Image> decodeAll(List<byte[]> images) {
        List<Image> out = new ArrayList<>();
        for (byte[] data : images) {
            if (GifSupport.isGif(data)) {
                out.add(ImageEdit.fromBufferedImage(GifSupport.readGif(data).get(0).image()));
            } else {
                out.add(ImageEdit.decode(data));
            }
        }
        return out;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(v, max));
    }
}
