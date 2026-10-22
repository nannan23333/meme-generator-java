package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Shader;
import io.github.humbleui.skija.Surface;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/fivethousand_choyen.rs（key: 5000choyen）：五千年文学大赏风格渐变描边文字。 */
@Component
public class ChoyenMeme extends AbstractMeme {

    public ChoyenMeme() {
        super(new MemeInfo("5000choyen",
                new MemeParams(0, 0, 2, 2, List.of("我去", "洛天依"), List.of()),
                List.of("5000兆"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 29, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 2, 0, 0), ZoneOffset.ofHours(8))));
    }

    /** 渐变色标。 */
    private record Stop(float pos, int rgb) {
    }

    private static int argb(int[] rgb) {
        return 0xFF000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
    }

    private static Paint gradientPaint(int[] dir, List<Stop> stops, boolean stroke, float strokeWidth) {
        int[] colors = stops.stream().mapToInt(s -> argb(new int[]{
                (s.rgb() >> 16) & 0xFF, (s.rgb() >> 8) & 0xFF, s.rgb() & 0xFF})).toArray();
        float[] positions = new float[stops.size()];
        for (int i = 0; i < stops.size(); i++) positions[i] = stops.get(i).pos();
        Shader shader = Shader.makeLinearGradient(
                dir[0], dir[1], dir[2], dir[3], colors, positions);
        Paint paint = new Paint().setShader(shader);
        if (stroke) {
            paint.setMode(io.github.humbleui.skija.PaintMode.STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setStrokeJoin(io.github.humbleui.skija.PaintStrokeJoin.ROUND);
        }
        return paint;
    }

    private static Paint colorPaint(int color, boolean stroke, float strokeWidth) {
        Paint paint = new Paint().setColor(color);
        if (stroke) {
            paint.setMode(io.github.humbleui.skija.PaintMode.STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setStrokeJoin(io.github.humbleui.skija.PaintStrokeJoin.ROUND);
        }
        return paint;
    }

    /**
     * 渲染文字层（含倾斜 transform），对齐 Rust transform() / add_color_text / add_gradient_text。
     * 关键点：倾斜是 <b>skewY</b>（y' = y + tilt*x）并把整体右移 dw，矩阵顺序按 skija Matrix33
     * 的 [scaleX, skewX, transX, skewY, scaleY, transY, 0, 0, 1] 排布。
     */
    private io.github.humbleui.skija.Image makeLayer(String text, float fontsize, String fontFile,
                                                     Paint fill, Paint stroke) {
        float tilt = -0.45f;
        var t2i = Text2Image.fromText(text, fontsize, fontFile, 0xFF000000, null, null)
                .withPaints(fill, stroke, 0f);
        float textW = t2i.longestLine();
        // Rust 的 text_h 来自 Skia Paragraph（≈ ascent + descent），不是 1.2*字号
        var fm = t2i.resolvedFont().getMetrics();
        float ascent = -fm.getAscent();
        float textH = ascent + fm.getDescent();
        float dw = Math.abs(textH * tilt);
        float padX = 20f;
        var surface = Surface.makeRaster(io.github.humbleui.skija.ImageInfo.makeN32Premul(
                Math.round(textW + dw + padX * 2), Math.round(textH) + 4));
        var canvas = surface.getCanvas();
        // 横向 shear（x' = x + tilt*y）并整体右移 dw，对齐 Rust 的 Matrix::from_affine
        canvas.concat(new io.github.humbleui.skija.Matrix33(
                new float[]{1f, tilt, dw, 0f, 1f, 0f, 0f, 0f, 1f}));
        // drawOnCanvas 的基线是 0.9*字号，Rust 是 ascent（200px 字号差 50+，会被裁掉顶部）
        t2i.drawOnCanvas(canvas, padX, ascent - fontsize * 0.9f);
        return surface.makeImageSnapshot();
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        float fontsize = 200f;
        List<Object[]> layers = new ArrayList<>(); // {Image, x, y}
        String sans = "NotoSansSC-Regular.ttf";
        String serif = "NotoSerifSC-Bold.otf";

        // ===== 第一段文字（Noto Sans SC）=====
        String text = texts.get(0);
        int posX = 20;
        int posY = 0;
        // 黑（粗描边 + 黑填充，偏移 +8,+8）
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                colorPaint(0xFF000000, false, 0),
                colorPaint(0xFF000000, true, 44f)), posX + 8, posY + 8});
        // 银（渐变，40 描边）
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                gradientPaint(new int[]{0, 38, 0, 234}, List.of(
                        new Stop(0f, rgb(0, 15, 36)), new Stop(0.1f, rgb(255, 255, 255)),
                        new Stop(0.18f, rgb(55, 58, 59)), new Stop(0.25f, rgb(55, 58, 59)),
                        new Stop(0.5f, rgb(200, 200, 200)), new Stop(0.75f, rgb(55, 58, 59)),
                        new Stop(0.85f, rgb(25, 20, 31)), new Stop(0.91f, rgb(240, 240, 240)),
                        new Stop(0.95f, rgb(166, 175, 194)), new Stop(1f, rgb(50, 50, 50))), false, 0),
                gradientPaint(new int[]{0, 38, 0, 234}, List.of(
                        new Stop(0f, rgb(0, 15, 36)), new Stop(0.1f, rgb(255, 255, 255)),
                        new Stop(0.18f, rgb(55, 58, 59)), new Stop(0.25f, rgb(55, 58, 59)),
                        new Stop(0.5f, rgb(200, 200, 200)), new Stop(0.75f, rgb(55, 58, 59)),
                        new Stop(0.85f, rgb(25, 20, 31)), new Stop(0.91f, rgb(240, 240, 240)),
                        new Stop(0.95f, rgb(166, 175, 194)), new Stop(1f, rgb(50, 50, 50))), true, 40f)),
                posX + 8, posY + 8});
        // 黑（32 描边 + 黑填充）
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                colorPaint(0xFF000000, false, 0),
                colorPaint(0xFF000000, true, 32f)), posX, posY});
        // 金（渐变，20 描边）
        List<Stop> gold = List.of(
                new Stop(0f, rgb(253, 241, 0)), new Stop(0.25f, rgb(245, 253, 187)),
                new Stop(0.4f, rgb(255, 255, 255)), new Stop(0.75f, rgb(253, 219, 9)),
                new Stop(0.9f, rgb(127, 53, 0)), new Stop(1f, rgb(243, 196, 11)));
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                gradientPaint(new int[]{0, 40, 0, 200}, gold, false, 0),
                gradientPaint(new int[]{0, 40, 0, 200}, gold, true, 20f)), posX, posY});
        // 黑（12 描边，偏移 +4,-6）
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                colorPaint(0xFF000000, false, 0),
                colorPaint(0xFF000000, true, 12f)), posX + 4, posY - 6});
        // 白（12 描边）
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                colorPaint(0xFFFFFFFF, false, 0),
                colorPaint(0xFFFFFFFF, true, 12f)), posX, posY - 6});
        // 红（渐变，8 描边）
        List<Stop> red = List.of(
                new Stop(0f, rgb(255, 100, 0)), new Stop(0.5f, rgb(123, 0, 0)),
                new Stop(0.51f, rgb(240, 0, 0)), new Stop(1f, rgb(5, 0, 0)));
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                gradientPaint(new int[]{0, 50, 0, 200}, red, false, 0),
                gradientPaint(new int[]{0, 50, 0, 200}, red, true, 8f)), posX, posY - 6});
        // 红（渐变，无描边）
        List<Stop> red2 = List.of(
                new Stop(0f, rgb(230, 0, 0)), new Stop(0.5f, rgb(123, 0, 0)),
                new Stop(0.51f, rgb(240, 0, 0)), new Stop(1f, rgb(5, 0, 0)));
        layers.add(new Object[]{makeLayer(text, fontsize, sans,
                gradientPaint(new int[]{0, 50, 0, 200}, red2, false, 0), null), posX, posY - 6});

        // ===== 第二段文字（Noto Serif SC）=====
        text = texts.get(1);
        posX = 280;
        posY = 260;
        // 黑 44
        layers.add(new Object[]{makeLayer(text, fontsize, serif,
                colorPaint(0xFF000000, false, 0),
                colorPaint(0xFF000000, true, 44f)), posX + 10, posY + 4});
        // 银 38 渐变
        List<Stop> silver2 = List.of(
                new Stop(0f, rgb(0, 15, 36)), new Stop(0.25f, rgb(250, 250, 250)),
                new Stop(0.5f, rgb(150, 150, 150)), new Stop(0.75f, rgb(55, 58, 59)),
                new Stop(0.85f, rgb(25, 20, 31)), new Stop(0.91f, rgb(240, 240, 240)),
                new Stop(0.95f, rgb(166, 175, 194)), new Stop(1f, rgb(50, 50, 50)));
        layers.add(new Object[]{makeLayer(text, fontsize, serif,
                gradientPaint(new int[]{0, 60, 0, 246}, silver2, false, 0),
                gradientPaint(new int[]{0, 60, 0, 246}, silver2, true, 38f)), posX + 10, posY + 4});
        // 黑蓝 34 描边 + 黑填充
        layers.add(new Object[]{makeLayer(text, fontsize, serif,
                colorPaint(0xFF10193A, false, 0),
                colorPaint(0xFF10193A, true, 34f)), posX, posY});
        // 白 16 描边
        layers.add(new Object[]{makeLayer(text, fontsize, serif,
                colorPaint(0xFFD0D0D0, false, 0),
                colorPaint(0xFFD0D0D0, true, 16f)), posX, posY});
        // 绀 14 渐变
        List<Stop> kon = List.of(
                new Stop(0f, rgb(16, 25, 58)), new Stop(0.03f, rgb(255, 255, 255)),
                new Stop(0.08f, rgb(16, 25, 58)), new Stop(0.2f, rgb(16, 25, 58)),
                new Stop(1f, rgb(16, 25, 58)));
        layers.add(new Object[]{makeLayer(text, fontsize, serif,
                gradientPaint(new int[]{0, 60, 0, 220}, kon, false, 0),
                gradientPaint(new int[]{0, 60, 0, 220}, kon, true, 14f)), posX, posY});
        // 银 渐变 无描边
        List<Stop> silver3 = List.of(
                new Stop(0f, rgb(245, 246, 248)), new Stop(0.15f, rgb(255, 255, 255)),
                new Stop(0.35f, rgb(195, 213, 220)), new Stop(0.5f, rgb(160, 190, 201)),
                new Stop(0.51f, rgb(160, 190, 201)), new Stop(0.52f, rgb(196, 215, 222)),
                new Stop(1f, rgb(255, 255, 255)));
        layers.add(new Object[]{makeLayer(text, fontsize, serif,
                gradientPaint(new int[]{0, 60, 0, 220}, silver3, false, 0), null), posX, posY - 6});

        // 合成
        int frameH = 580;
        int frameW = 0;
        for (Object[] l : layers) {
            var img = (io.github.humbleui.skija.Image) l[0];
            int x = (int) l[1];
            frameW = Math.max(frameW, img.getWidth() + x);
        }
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        for (Object[] l : layers) {
            canvas.drawImage((io.github.humbleui.skija.Image) l[0], (int) l[1], (int) l[2]);
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }

    private static int rgb(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
}
