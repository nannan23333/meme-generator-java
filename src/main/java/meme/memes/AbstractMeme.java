package meme.memes;

import meme.core.Image;
import meme.core.Meme;
import meme.core.MemeError;
import meme.core.MemeInfo;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.TextRenderer;
import io.github.humbleui.skija.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 表情基类：统一实现参数校验（550/551）、图片解码、预览占位逻辑，
 * 对齐 Rust MemeBuilder 的 generate / generate_preview 行为。
 */
public abstract class AbstractMeme implements Meme {

    private final MemeInfo info;

    protected AbstractMeme(MemeInfo info) {
        this.info = info;
    }

    /** 子类实现具体绘制。 */
    protected abstract byte[] doGenerate(List<InputImage> images, List<String> texts,
                                         Map<String, Object> options);

    @Override
    public final String key() {
        return info.key();
    }

    @Override
    public final MemeInfo info() {
        return info;
    }

    @Override
    public final byte[] generate(List<Image> images, List<String> texts, Map<String, Object> options) {
        var params = info.params();
        if (images.size() < params.minImages() || images.size() > params.maxImages()) {
            throw MemeError.imageNumberMismatch(params.minImages(), params.maxImages(), images.size());
        }
        if (texts.size() < params.minTexts() || texts.size() > params.maxTexts()) {
            throw MemeError.textNumberMismatch(params.minTexts(), params.maxTexts(), texts.size());
        }
        List<InputImage> inputs = new ArrayList<>();
        for (Image image : images) {
            inputs.add(InputImage.from(image));
        }
        return doGenerate(inputs, texts, options == null ? Map.of() : options);
    }

    @Override
    public final byte[] generatePreview(Map<String, Object> options) {
        var params = info.params();
        List<Image> images = new ArrayList<>();
        if (params.minImages() > 0) {
            byte[] placeholder = PlaceholderImages.gridPatternPng();
            for (int i = 0; i < params.minImages(); i++) {
                String name = params.minImages() == 1
                        ? "{name}"
                        : "{name%d}".formatted(i + 1);
                images.add(new Image(name, placeholder));
            }
        }
        List<String> texts;
        if (params.defaultTexts().size() >= params.minTexts()
                && params.defaultTexts().size() <= params.maxTexts()) {
            texts = params.defaultTexts();
        } else {
            texts = new ArrayList<>();
            for (int i = 0; i < params.minTexts(); i++) {
                texts.add(params.minTexts() == 1 ? "{text}" : "{text%d}".formatted(i + 1));
            }
        }
        return generate(images, texts, options == null ? Map.of() : options);
    }

    // ---------- 选项读取辅助 ----------

    protected static boolean boolOption(Map<String, Object> options, String name, boolean defaultValue) {
        Object v = options.get(name);
        if (v instanceof Boolean b) {
            return b;
        }
        if (v instanceof String s) {
            return Boolean.parseBoolean(s);
        }
        if (v instanceof Number n) {
            return n.intValue() != 0;
        }
        return defaultValue;
    }

    protected static String stringOption(Map<String, Object> options, String name, String defaultValue) {
        Object v = options.get(name);
        return v == null ? defaultValue : v.toString();
    }

    protected static int intOption(Map<String, Object> options, String name, int defaultValue) {
        Object v = options.get(name);
        if (v instanceof Number n) {
            return n.intValue();
        }
        if (v instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    protected static float floatOption(Map<String, Object> options, String name, float defaultValue) {
        Object v = options.get(name);
        if (v instanceof Number n) {
            return n.floatValue();
        }
        if (v instanceof String s) {
            try {
                return Float.parseFloat(s.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    // ---------- GIF 辅助 ----------

    /**
     * 对齐 make_gif_or_combined_gif（简化版）：
     * 输入为 GIF 时按帧循环对齐（ExtendLoop 语义），静态图则直接循环 frameNum 帧。
     */
    /**
     * 输出 GIF：frameNum 帧。
     * renderer 第一个参数 = 输出帧号（决定模板帧），第二个参数 = 输入 GIF 帧号（ExtendLoop 对齐）。
     */
    protected static byte[] makeGifLoop(InputImage input,
                                        java.util.function.BiFunction<Integer, Integer, io.github.humbleui.skija.Image> frameRenderer,
                                        int frameNum, float duration) {
        List<GifSupport.GifFrame> inputFrames = input.gifFrames();
        int inputFrameCount = (inputFrames == null) ? 1 : inputFrames.size();
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < frameNum; i++) {
            frames.add(frameRenderer.apply(i, i % inputFrameCount));
        }
        return GifSupport.writeGifFromSkija(frames, duration);
    }

    /** 占位图生成器（网格棋盘图，对齐 Rust GRID_PATTERN_IMAGE 用途）。 */
    static final class PlaceholderImages {
        private static byte[] gridPatternPng() {
            int size = 500;
            try (var surface = TextRenderer.newSurface(size, size)) {
                Canvas canvas = surface.getCanvas();
                canvas.clear(0xFFF0F0F0);
                try (var paint = new io.github.humbleui.skija.Paint().setColor(0xFFCCCCCC)) {
                    int cell = 50;
                    for (int y = 0; y < size; y += cell) {
                        for (int x = 0; x < size; x += cell) {
                            if (((x / cell) + (y / cell)) % 2 == 0) {
                                canvas.drawRect(io.github.humbleui.types.Rect
                                        .makeXYWH(x, y, cell, cell), paint);
                            }
                        }
                    }
                }
                return ImageEdit.encodePng(surface.makeImageSnapshot());
            }
        }
    }
}
