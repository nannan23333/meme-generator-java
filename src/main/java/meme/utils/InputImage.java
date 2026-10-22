package meme.utils;

import io.github.humbleui.skija.Image;
import meme.core.MemeError;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 输入图片（解码后的 skija Image + GIF 帧信息），对齐 Rust InputImage。
 */
public final class InputImage {

    private final String name;
    private final Image firstFrame;
    private final boolean multiFrame;
    private final List<GifSupport.GifFrame> gifFrames; // 仅 GIF 有

    private InputImage(String name, Image firstFrame, List<GifSupport.GifFrame> gifFrames) {
        this.name = name;
        this.firstFrame = firstFrame;
        this.gifFrames = gifFrames;
        this.multiFrame = gifFrames != null && gifFrames.size() > 1;
    }

    public static InputImage from(meme.core.Image image) {
        byte[] data = image.data();
        if (GifSupport.isGif(data)) {
            List<GifSupport.GifFrame> frames = GifSupport.readGif(data);
            Image first = ImageEdit.fromBufferedImage(frames.get(0).image());
            return new InputImage(image.name(), first, frames);
        }
        Image decoded = ImageEdit.decode(data);
        return new InputImage(image.name(), decoded, null);
    }

    public String name() {
        return name;
    }

    /** 首帧。 */
    public Image image() {
        return firstFrame;
    }

    public boolean isMultiFrame() {
        return multiFrame;
    }

    /** GIF 帧列表（非 GIF 返回 null）。 */
    public List<GifSupport.GifFrame> gifFrames() {
        return gifFrames;
    }

    /** 平均帧时长（秒），非 GIF 返回 0。 */
    public float averageDuration() {
        if (!multiFrame) {
            return 0;
        }
        return GifSupport.averageDuration(gifFrames);
    }

    /**
     * 逐帧处理：静态图 → 对首帧应用 func 输出 PNG；
     * GIF → 逐帧应用 func 输出 GIF（保持原帧延时）。
     * 对齐 Rust make_png_or_gif。
     */
    public static byte[] makePngOrGif(InputImage input, java.util.function.Function<Image, Image> func) {
        if (!input.isMultiFrame()) {
            return ImageEdit.encodePng(func.apply(input.image()));
        }
        List<BufferedImage> out = new ArrayList<>();
        int[] delays = new int[input.gifFrames().size()];
        for (int i = 0; i < input.gifFrames().size(); i++) {
            GifSupport.GifFrame f = input.gifFrames().get(i);
            Image frame = ImageEdit.fromBufferedImage(f.image());
            Image processed = func.apply(frame);
            out.add(ImageEdit.toBufferedImage(processed));
            delays[i] = f.delayMs();
        }
        return GifSupport.writeGif(out, delays);
    }
}
