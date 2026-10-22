package meme.utils;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import io.github.humbleui.skija.Image;
import meme.core.MemeError;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GIF 读写支持：读（TwelveMonkeys，逐帧 + 帧延时），写（animated-gif-lib）。
 * 对齐 Rust decoder.rs / encoder.rs（gifski 的替代实现）。
 */
public final class GifSupport {

    public record GifFrame(BufferedImage image, int delayMs) {
    }

    private GifSupport() {
    }

    public static boolean isGif(byte[] data) {
        return data.length > 6
                && data[0] == 'G' && data[1] == 'I' && data[2] == 'F' && data[3] == '8';
    }

    /**
     * 解码 GIF 全部帧（延时单位毫秒）。非 GIF 或解码失败抛 510。
     * <p>JDK 的 {@code reader.read(i)} 只返回帧矩形内的原始数据，<b>不会</b>按 disposal 合成前帧，
     * 因此这里按 GIF 规范手工合成（Rust 侧 Skia 的 SkCodec 是自动合成的）。</p>
     */
    public static List<GifFrame> readGif(byte[] data) {
        List<GifFrame> frames = new ArrayList<>();
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw MemeError.imageDecode("no gif reader");
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis);
                int n = reader.getNumImages(true);
                int w = reader.getWidth(0);
                int h = reader.getHeight(0);
                BufferedImage canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = canvas.createGraphics();
                g.setComposite(AlphaComposite.SrcOver);
                for (int i = 0; i < n; i++) {
                    IIOMetadata md = reader.getImageMetadata(i);
                    IIOMetadataNode gce = childNode(md, "GraphicControlExtension");
                    IIOMetadataNode desc = childNode(md, "ImageDescriptor");
                    int disposal = gce == null ? 0 : disposalOf(gce.getAttribute("disposalMethod"));
                    int delay = gce == null ? 100 : delayOf(gce.getAttribute("delayTime"));
                    int fx = desc == null ? 0 : intAttr(desc, "imageLeftPosition");
                    int fy = desc == null ? 0 : intAttr(desc, "imageTopPosition");
                    int fw = desc == null ? w : intAttr(desc, "imageWidth");
                    int fh = desc == null ? h : intAttr(desc, "imageHeight");

                    BufferedImage raw = toArgb(reader.read(i));
                    BufferedImage saved = disposal == 3 ? copyOf(canvas) : null;
                    // JDK 对不同模板可能返回整幅或仅帧矩形，两种都兼容
                    if (raw.getWidth() == w && raw.getHeight() == h) {
                        g.drawImage(raw, 0, 0, null);
                    } else {
                        g.drawImage(raw, fx, fy, null);
                    }
                    frames.add(new GifFrame(copyOf(canvas), delay));

                    if (disposal == 2) { // restore to background
                        var old = g.getComposite();
                        g.setComposite(AlphaComposite.Clear);
                        g.fillRect(fx, fy, fw, fh);
                        g.setComposite(old);
                    } else if (disposal == 3 && saved != null) { // restore to previous
                        g.dispose();
                        canvas = saved;
                        g = canvas.createGraphics();
                        g.setComposite(AlphaComposite.SrcOver);
                    }
                }
                g.dispose();
            } finally {
                reader.dispose();
            }
        } catch (MemeError e) {
            throw e;
        } catch (Exception e) {
            throw MemeError.imageDecode(e.getMessage());
        }
        if (frames.isEmpty()) {
            throw MemeError.imageDecode("gif has no frames");
        }
        return frames;
    }

    private static IIOMetadataNode childNode(IIOMetadata metadata, String name) {
        try {
            IIOMetadataNode root = (IIOMetadataNode) metadata
                    .getAsTree("javax_imageio_gif_image_1.0");
            var children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                IIOMetadataNode node = (IIOMetadataNode) children.item(i);
                if (name.equals(node.getNodeName())) {
                    return node;
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private static int intAttr(IIOMetadataNode node, String name) {
        String v = node.getAttribute(name);
        if (v == null || v.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int disposalOf(String v) {
        if (v == null) {
            return 0;
        }
        return switch (v.trim()) {
            case "doNotDispose", "none", "0", "1" -> 1;
            case "restoreToBackgroundColor", "2" -> 2;
            case "restoreToPrevious", "3" -> 3;
            default -> 0;
        };
    }

    /** delayTime 单位是 10ms，Rust/Skia 侧不小于 20ms。 */
    private static int delayOf(String v) {
        if (v == null || v.isBlank()) {
            return 100;
        }
        try {
            return Math.max(20, Integer.parseInt(v.trim()) * 10);
        } catch (NumberFormatException e) {
            return 100;
        }
    }

    private static BufferedImage toArgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) {
            return src;
        }
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dst;
    }

    private static BufferedImage copyOf(BufferedImage src) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dst;
    }

    /** 平均帧时长（秒），对齐 Rust get_average_duration。 */
    public static float averageDuration(List<GifFrame> frames) {
        float total = 0;
        for (GifFrame f : frames) {
            total += f.delayMs() / 1000f;
        }
        return Math.max(0.02f, total / frames.size());
    }

    /**
     * 编码 GIF（无限循环）。delays 与 frames 等长（毫秒）。
     * <p>注意：<b>不做</b>重复帧合并。Rust 侧 gifski 会合并部分重复帧，但合并规则不可复现
     * （实测同一序列里它只合并了尾部一对），强行合并反而会让 Java 的时间轴与 Rust 错开。</p>
     * <p>含透明像素的序列会写透明索引（animated-gif-lib 自身丢 alpha，这里用哨兵色 +
     * {@code setTransparent} 保住透明背景，并配 disposal=2 避免残影）。</p>
     */
    public static byte[] writeGif(List<BufferedImage> frames, int[] delaysMs) {
        boolean hasAlpha = false;
        for (BufferedImage f : frames) {
            if (hasTransparentPixel(f)) {
                hasAlpha = true;
                break;
            }
        }
        List<BufferedImage> prepared = new ArrayList<>(frames.size());
        for (BufferedImage f : frames) {
            prepared.add(hasAlpha ? flatten(f) : f);
        }
        // 全局调色板：所有帧共用一个（animated-gif-lib 只写首帧全局调色板，后续帧自己算的
        // NeuQuant 调色板不会被写进文件，颜色会被映射回首帧调色板 → 头部变灰/变色）。
        int[] palette = buildPalette(prepared, hasAlpha);
        PaletteGifEncoder encoder = new PaletteGifEncoder(palette, PALETTE_COLORS);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encoder.start(bos);
        encoder.setRepeat(0);
        if (hasAlpha) {
            encoder.setTransparent(TRANSPARENT_SENTINEL);
            encoder.setDispose(2);
        }
        for (int i = 0; i < prepared.size(); i++) {
            int delay = Math.max(20, i < delaysMs.length ? delaysMs[i] : 100);
            encoder.setDelay(delay);
            encoder.addFrame(prepared.get(i));
        }
        encoder.finish();
        return bos.toByteArray();
    }

    /** 真实颜色数（另一格留给透明哨兵）。 */
    private static final int PALETTE_COLORS = 255;

    /**
     * 用中位切分（median cut）在<b>全部帧</b>上建一个 256 色调色板：
     * 前 {@link #PALETTE_COLORS} 个是真实颜色，最后一格固定为透明哨兵。
     */
    private static int[] buildPalette(List<BufferedImage> frames, boolean withSentinel) {
        final int levels = 32;                       // 每通道 5 bit
        final int buckets = levels * levels * levels; // 32768
        int[] count = new int[buckets];
        long[] sr = new long[buckets];
        long[] sg = new long[buckets];
        long[] sb = new long[buckets];
        for (BufferedImage img : frames) {
            int w = img.getWidth();
            int h = img.getHeight();
            int[] px = pixelsOf(img);
            for (int i = 0, n = w * h; i < n; i++) {
                int p = px[i];
                if ((p & 0xFFFFFF) == 0xFF00FF) {
                    continue; // 哨兵（透明）
                }
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                int idx = ((r >> 3) << 10) | ((g >> 3) << 5) | (b >> 3);
                count[idx]++;
                sr[idx] += r;
                sg[idx] += g;
                sb[idx] += b;
            }
        }
        // 只保留非空桶
        int used = 0;
        for (int i = 0; i < buckets; i++) {
            if (count[i] > 0) {
                used++;
            }
        }
        int[] ord = new int[used];
        for (int i = 0, k = 0; i < buckets; i++) {
            if (count[i] > 0) {
                ord[k++] = i;
            }
        }
        // 盒子：[lo, hi) 区间
        java.util.ArrayList<int[]> boxes = new java.util.ArrayList<>();
        boxes.add(new int[]{0, used});
        while (boxes.size() < PALETTE_COLORS) {
            int best = -1;
            long bestScore = 0;
            for (int i = 0; i < boxes.size(); i++) {
                int[] box = boxes.get(i);
                if (box[1] - box[0] < 2) {
                    continue;
                }
                long cnt = 0;
                int rMin = 255, rMax = 0, gMin = 255, gMax = 0, bMin = 255, bMax = 0;
                for (int k = box[0]; k < box[1]; k++) {
                    int idx = ord[k];
                    int r = (int) (sr[idx] / count[idx]);
                    int g = (int) (sg[idx] / count[idx]);
                    int b = (int) (sb[idx] / count[idx]);
                    rMin = Math.min(rMin, r); rMax = Math.max(rMax, r);
                    gMin = Math.min(gMin, g); gMax = Math.max(gMax, g);
                    bMin = Math.min(bMin, b); bMax = Math.max(bMax, b);
                    cnt += count[idx];
                }
                int range = Math.max(rMax - rMin, Math.max(gMax - gMin, bMax - bMin));
                long score = range * cnt;
                if (score > bestScore) {
                    bestScore = score;
                    best = i;
                }
            }
            if (best < 0) {
                break;
            }
            int[] box = boxes.get(best);
            // 找出最宽通道并按其排序
            int rMin = 255, rMax = 0, gMin = 255, gMax = 0, bMin = 255, bMax = 0;
            for (int k = box[0]; k < box[1]; k++) {
                int idx = ord[k];
                int r = (int) (sr[idx] / count[idx]);
                int g = (int) (sg[idx] / count[idx]);
                int b = (int) (sb[idx] / count[idx]);
                rMin = Math.min(rMin, r); rMax = Math.max(rMax, r);
                gMin = Math.min(gMin, g); gMax = Math.max(gMax, g);
                bMin = Math.min(bMin, b); bMax = Math.max(bMax, b);
            }
            int ch = 0;
            int range = rMax - rMin;
            if (gMax - gMin > range) { ch = 1; range = gMax - gMin; }
            if (bMax - bMin > range) { ch = 2; }
            final int channel = ch;
            int len = box[1] - box[0];
            long[] keys = new long[len];
            for (int k = 0; k < len; k++) {
                int idx = ord[box[0] + k];
                int v = channel == 0 ? (int) (sr[idx] / count[idx])
                        : channel == 1 ? (int) (sg[idx] / count[idx])
                        : (int) (sb[idx] / count[idx]);
                keys[k] = ((long) v << 32) | idx;
            }
            java.util.Arrays.sort(keys);
            long total = 0;
            for (int k = 0; k < len; k++) {
                ord[box[0] + k] = (int) keys[k];
                total += count[(int) keys[k]];
            }
            long acc = 0;
            int splitAt = box[0] + len / 2;
            for (int k = box[0]; k < box[1]; k++) {
                acc += count[ord[k]];
                if (acc * 2 >= total) {
                    splitAt = k + 1;
                    break;
                }
            }
            splitAt = Math.max(box[0] + 1, Math.min(box[1] - 1, splitAt));
            boxes.set(best, new int[]{box[0], splitAt});
            boxes.add(new int[]{splitAt, box[1]});
        }
        int[] pal = new int[256];
        int n = 0;
        for (int[] box : boxes) {
            if (n >= PALETTE_COLORS) {
                break;
            }
            long cnt = 0, r = 0, g = 0, b = 0;
            for (int k = box[0]; k < box[1]; k++) {
                int idx = ord[k];
                cnt += count[idx];
                r += sr[idx];
                g += sg[idx];
                b += sb[idx];
            }
            if (cnt == 0) {
                continue;
            }
            pal[n++] = (int) (r / cnt) << 16 | (int) (g / cnt) << 8 | (int) (b / cnt);
        }
        for (int i = n; i < PALETTE_COLORS; i++) {
            pal[i] = 0xFFFFFF;
        }
        pal[255] = SENTINEL_RGB & 0xFFFFFF;
        return pal;
    }

    /** 取像素（尽量复用底层数组）。 */
    private static int[] pixelsOf(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (img.getRaster().getDataBuffer() instanceof DataBufferInt db && db.getNumBanks() == 1) {
            int[] data = db.getData();
            if (data.length >= w * h) {
                return data;
            }
        }
        return img.getRGB(0, 0, w, h, null, 0, w);
    }

    /**
     * 用固定调色板编码：覆盖 analyzePixels，直接按最近色建索引，
     * 不再每帧跑 NeuQuant（既保证跨帧颜色一致，也省掉大量 CPU）。
     */
    private static final class PaletteGifEncoder extends AnimatedGifEncoder {
        private final byte[] pal = new byte[768];
        private final int[] palInt;
        private final int transIdx;
        private final java.util.HashMap<Integer, Integer> cache = new java.util.HashMap<>();

        PaletteGifEncoder(int[] colors, int colorCount) {
            this.palInt = colors;
            for (int i = 0; i < 256; i++) {
                int c = colors[i];
                pal[i * 3] = (byte) ((c >> 16) & 0xFF);
                pal[i * 3 + 1] = (byte) ((c >> 8) & 0xFF);
                pal[i * 3 + 2] = (byte) (c & 0xFF);
            }
            this.transIdx = 255;
            this.realColors = Math.max(1, Math.min(PALETTE_COLORS, colorCount));
            this.colorTab = pal;
            this.colorDepth = 8;
            this.palSize = 7;
            this.usedEntry = new boolean[256];
            java.util.Arrays.fill(usedEntry, true);
        }

        private final int realColors;

        @Override
        protected void analyzePixels() {
            int n = pixels.length / 3;
            byte[] out = new byte[n];
            for (int i = 0; i < n; i++) {
                // getImagePixels() 按 B,G,R 顺序填像素（库内部 NeuQuant 也按 BGR 解析）
                int b = pixels[i * 3] & 0xFF;
                int g = pixels[i * 3 + 1] & 0xFF;
                int r = pixels[i * 3 + 2] & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                if (rgb == (SENTINEL_RGB & 0xFFFFFF)) {
                    out[i] = (byte) transIdx;
                    continue;
                }
                Integer c = cache.get(rgb);
                if (c == null) {
                    c = nearest(rgb);
                    cache.put(rgb, c);
                }
                out[i] = (byte) (int) c;
            }
            indexedPixels = out;
            pixels = null;
            colorTab = pal;
            colorDepth = 8;
            palSize = 7;
            transIndex = transIdx;
        }

        private int nearest(int rgb) {
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            int best = 0;
            int bestDist = Integer.MAX_VALUE;
            for (int i = 0; i < realColors; i++) {
                int c = palInt[i];
                int dr = r - ((c >> 16) & 0xFF);
                int dg = g - ((c >> 8) & 0xFF);
                int db = b - (c & 0xFF);
                int d = dr * dr + dg * dg + db * db;
                if (d < bestDist) {
                    bestDist = d;
                    best = i;
                    if (d == 0) {
                        break;
                    }
                }
            }
            return best;
        }
    }

    /** 透明哨兵色（洋红），仅用于 GIF 索引，正常表情不会大面积出现该色。 */
    private static final java.awt.Color TRANSPARENT_SENTINEL = new java.awt.Color(255, 0, 255);
    private static final int SENTINEL_RGB = 0xFFFF00FF;

    private static boolean hasTransparentPixel(BufferedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            return false;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        if (img.getRaster().getDataBuffer() instanceof DataBufferInt db) {
            int[] px = db.getData();
            int off = img.getRaster().getDataBuffer().getOffset();
            for (int i = off; i < px.length; i++) {
                if ((px[i] >>> 24) < 128) {
                    return true;
                }
            }
            return false;
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((img.getRGB(x, y) >>> 24) < 128) {
                    return true;
                }
            }
        }
        return false;
    }

    /** alpha 二值化并铺哨兵色（GIF 只支持 1 位透明）。 */
    private static BufferedImage flatten(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[] dst = ((DataBufferInt) out.getRaster().getDataBuffer()).getData();
        int[] src = null;
        if (img.getRaster().getDataBuffer() instanceof DataBufferInt db) {
            src = db.getData();
        }
        if (src != null && src.length >= w * h) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int p = src[y * w + x];
                    dst[y * w + x] = ((p >>> 24) < 128) ? SENTINEL_RGB : (p | 0xFF000000);
                }
            }
        } else {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int p = img.getRGB(x, y);
                    out.setRGB(x, y, ((p >>> 24) < 128) ? SENTINEL_RGB : (p | 0xFF000000));
                }
            }
        }
        return out;
    }

    /** 便捷方法：统一帧时长（秒）。 */
    public static byte[] writeGifUniform(List<BufferedImage> frames, float durationSeconds) {
        int[] delays = new int[frames.size()];
        int ms = Math.round(durationSeconds * 1000);
        java.util.Arrays.fill(delays, Math.max(20, ms));
        return writeGif(frames, delays);
    }

    /** skija 帧 → GIF。 */
    public static byte[] writeGifFromSkija(List<Image> frames, float durationSeconds) {
        List<BufferedImage> imgs = new ArrayList<>();
        for (Image f : frames) {
            imgs.add(ImageEdit.toBufferedImage(f));
        }
        return writeGifUniform(imgs, durationSeconds);
    }
}
