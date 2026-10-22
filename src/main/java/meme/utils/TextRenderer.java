package meme.utils;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Color4f;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.types.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字渲染：自动字号、描边、居中排版。对齐 Rust canvas.draw_text_area_auto_font_size 等能力。
 */
public final class TextRenderer {

    private TextRenderer() {
    }

    /** 文本绘制参数（fluent builder）。 */
    public static final class Params {
        String[] fontFamilies = {};
        float strokeRatio = 0f;          // 描边宽度 = 字号 * ratio，0 表示无描边
        float strokePx = 0f;             // 绝对像素描边宽度（优先于 ratio），对齐 Rust new_stroke_paint(color, px)
        int strokeColor = 0xFF000000;
        int fillColor = 0xFF000000;
        float lineHeightRatio = 1.2f;
        Align align = Align.CENTER;

        public enum Align { LEFT, CENTER, RIGHT }

        public Params fontFamilies(String... families) {
            this.fontFamilies = families;
            return this;
        }

        public Params stroke(float ratio, int color) {
            this.strokeRatio = ratio;
            this.strokeColor = color;
            return this;
        }

        /** 绝对像素描边宽度，对齐 Rust new_stroke_paint(color, width)。 */
        public Params strokePx(float px, int color) {
            this.strokePx = px;
            this.strokeColor = color;
            return this;
        }

        public Params fill(int color) {
            this.fillColor = color;
            return this;
        }

        public Params lineHeight(float ratio) {
            this.lineHeightRatio = ratio;
            return this;
        }

        public Params align(Align a) {
            this.align = a;
            return this;
        }
    }

    public static Params params() {
        return new Params();
    }

    /**
     * 行高：对齐 Skia Paragraph（≈ ascent + descent），而不是固定 1.2*字号。
     * 两者在 40px 下差 ~10px，会让"自动字号"选出不同档位（例如本应单行却换成两行）。
     */
    public static float lineHeight(Font font) {
        var m = font.getMetrics();
        if (m == null) {
            return font.getSize() * 1.2f;
        }
        float h = -m.getAscent() + m.getDescent();
        return h > 0 ? h : font.getSize() * 1.2f;
    }

    private static Font pickFont(Params p, float size) {
        for (String family : p.fontFamilies) {
            return FontManager.font(family, size);
        }
        return new Font(FontManager.defaultTypeface(), size);
    }

    /**
     * 在 box 内自动字号绘制文本（从 maxSize 递减到 minSize，首个能放下的字号生效），
     * 水平垂直居中；minSize 仍放不下时抛 TEXT_OVER_LENGTH。
     */
    public static void drawTextAreaAutoFontSize(Canvas canvas, Rect box, String text,
                                                float minSize, float maxSize, Params p) {
        List<String> lines = null;
        Font used = null;
        for (float size = maxSize; size >= minSize; size -= 1f) {
            Font font = pickFont(p, size);
            List<String> wrapped = wrap(font, text, box.getWidth());
            // 判定是否放得下用字体度量行高（≒ Skia Paragraph），绘制仍按 1.2*字号，
            // 否则普通单行文案的垂直居中会整体偏移
            float lineH = lineHeight(font);
            float totalH = wrapped.size() * lineH;
            boolean fits = wrapped.stream().noneMatch(l -> width(font, l) > box.getWidth() + 0.5f)
                    && totalH <= box.getHeight() + 0.5f;
            if (fits) {
                lines = wrapped;
                used = font;
                break;
            }
            if (size - 1f < minSize && lines == null) {
                // 最后一次尝试（minSize），放不下则报错
                if (wrapped.stream().noneMatch(l -> width(font, l) > box.getWidth() + 0.5f)
                        && totalH <= box.getHeight() + 0.5f) {
                    lines = wrapped;
                    used = font;
                }
            }
        }
        if (lines == null) {
            throw meme.core.MemeError.textOverLength(text);
        }
        drawLinesCentered(canvas, lines, used, box, p);
    }

    /** 单行文字（自动水平居中于 box），用于简单场景。 */
    public static void drawTextCentered(Canvas canvas, String text, Rect box, float size, Params p) {
        Font font = pickFont(p, size);
        float w = width(font, text);
        float x = box.getLeft() + (box.getWidth() - w) / 2f;
        float lineH = size * p.lineHeightRatio;
        float y = box.getTop() + (box.getHeight() - lineH) / 2f + size * 0.9f;
        drawStringWithStroke(canvas, text, x, y, font, p);
    }

    /**
     * 固定字号绘制（对齐 Rust draw_text_area）：按 box 宽自动换行，垂直居中，支持 \n。
     * 注意：不做字号收缩，超长不报错。
     */
    public static void drawTextArea(Canvas canvas, Rect box, String text, float fontSize, Params p) {
        Font font = pickFont(p, fontSize);
        List<String> lines = wrap(font, text, box.getWidth());
        drawLinesCentered(canvas, lines, font, box, p);
    }

    /** 在指定坐标绘制单行文字（x 为左边界，y 为文字顶部基线偏移），对齐 Rust draw_text。 */
    public static void drawText(Canvas canvas, float x, float y, String text, float fontSize, Params p) {
        Font font = pickFont(p, fontSize);
        drawStringWithStroke(canvas, text, x, y + fontSize * 0.9f, font, p);
    }

    static void drawLinesCentered(Canvas canvas, List<String> lines, Font font, Rect box, Params p) {
        float lineH = font.getSize() * p.lineHeightRatio;
        float totalH = lines.size() * lineH;
        float startY = box.getTop() + (box.getHeight() - totalH) / 2f;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            float w = width(font, line);
            float x = switch (p.align == null ? Params.Align.CENTER : p.align) {
                case LEFT -> box.getLeft();
                case RIGHT -> box.getLeft() + box.getWidth() - w;
                case CENTER -> box.getLeft() + (box.getWidth() - w) / 2f;
            };
            float y = startY + i * lineH + font.getSize() * 0.9f;
            drawStringWithStroke(canvas, line, x, y, font, p);
        }
    }

    public static void drawStringWithStroke(Canvas canvas, String text, float x, float y,
                                            Font font, Params p) {
        if (p.strokeRatio > 0 || p.strokePx > 0) {
            float strokeWidth = p.strokePx > 0 ? p.strokePx : font.getSize() * p.strokeRatio;
            try (Paint stroke = new Paint()
                    .setMode(io.github.humbleui.skija.PaintMode.STROKE)
                    .setStrokeWidth(strokeWidth)
                    .setAntiAlias(true)
                    .setColor(p.strokeColor)) {
                drawStringAuto(canvas, text, x, y, font, stroke);
            }
        }
        try (Paint fill = new Paint().setAntiAlias(true).setColor(p.fillColor)) {
            drawStringAuto(canvas, text, x, y, font, fill);
        }
    }

    static List<String> wrap(Font font, String text, float maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
        for (String paragraph : text.split("\n", -1)) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            StringBuilder current = new StringBuilder();
            for (int i = 0; i < paragraph.length(); ) {
                int cp = paragraph.codePointAt(i);
                int len = Character.charCount(cp);
                String ch = paragraph.substring(i, i + len);
                String candidate = current + ch;
                if (current.length() > 0 && width(font, candidate) > maxWidth) {
                    lines.add(current.toString());
                    current.setLength(0);
                    current.append(ch);
                } else {
                    current.append(ch);
                }
                i += len;
            }
            if (current.length() > 0) {
                lines.add(current.toString());
            }
        }
        return lines;
    }

    /**
     * 文本宽度：用 <b>advance 宽度</b>（字宽累加），对齐 Rust/Skia Paragraph 的 longest_line。
     * 注意别用 measureText 的 ink 包围盒（right-left）——它不含左右边距，
     * 每串会窄 20~30px，导致画布宽度、换行位置与 Rust 不一致。
     */
    public static float width(Font font, String text) {
        return measureAuto(font, text);
    }

    /** 回退字体（主字体缺字时使用），对齐 Rust 的 font_families 回退链。 */
    public static io.github.humbleui.skija.Typeface fallbackTypeface() {
        return FontManager.defaultTypeface();
    }

    /**
     * 逐字符测量宽度：主字体没有该字形时用回退字体量，避免整串按豆腐块宽度排版。
     */
    public static float measureAuto(Font font, String text) {
        return measureAuto(font, text, fallbackTypeface());
    }

    public static float measureAuto(Font font, String text, io.github.humbleui.skija.Typeface fallback) {
        if (text == null || text.isEmpty()) {
            return 0f;
        }
        io.github.humbleui.skija.Typeface primary = font.getTypeface();
        io.github.humbleui.skija.Typeface fb = fallback != null ? fallback : fallbackTypeface();
        if (primary == null || fb == primary || hasAllGlyphs(primary, text)) {
            return font.measureTextWidth(text);
        }
        Font fbFont = new Font(fb, font.getSize());
        float total = 0f;
        int i = 0;
        int n = text.length();
        while (i < n) {
            int cp = text.codePointAt(i);
            boolean missing = primary.getUTF32Glyph(cp) == 0 && cp != ' ' && cp != '\t';
            int start = i;
            while (i < n) {
                int c = text.codePointAt(i);
                boolean m = primary.getUTF32Glyph(c) == 0 && c != ' ' && c != '\t';
                if (m != missing) {
                    break;
                }
                i += Character.charCount(c);
            }
            String run = text.substring(start, i);
            total += (missing ? fbFont : font).measureTextWidth(run);
        }
        return total;
    }

    private static boolean hasAllGlyphs(io.github.humbleui.skija.Typeface tf, String text) {
        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);
            if (cp != ' ' && cp != '\t' && tf.getUTF32Glyph(cp) == 0) {
                return false;
            }
            i += Character.charCount(cp);
        }
        return true;
    }

    /**
     * 绘制文字，主字体缺字时自动切到回退字体（对齐 Rust 的 font_families 回退：
     * Skia Paragraph 会逐字回退，我们这里手工按 run 拆分）。
     */
    public static void drawStringAuto(Canvas canvas, String text, float x, float y,
                                      Font font, Paint paint) {
        drawStringAuto(canvas, text, x, y, font, paint, fallbackTypeface());
    }

    public static void drawStringAuto(Canvas canvas, String text, float x, float y,
                                      Font font, Paint paint,
                                      io.github.humbleui.skija.Typeface fallback) {
        io.github.humbleui.skija.Typeface primary = font.getTypeface();
        io.github.humbleui.skija.Typeface fb = fallback != null ? fallback : fallbackTypeface();
        if (text == null || text.isEmpty() || primary == null || fb == primary
                || hasAllGlyphs(primary, text)) {
            canvas.drawString(text, x, y, font, paint);
            return;
        }
        Font fbFont = new Font(fb, font.getSize());
        float cx = x;
        int i = 0;
        int n = text.length();
        while (i < n) {
            int cp = text.codePointAt(i);
            boolean missing = primary.getUTF32Glyph(cp) == 0 && cp != ' ' && cp != '\t';
            int start = i;
            while (i < n) {
                int c = text.codePointAt(i);
                boolean m = primary.getUTF32Glyph(c) == 0 && c != ' ' && c != '\t';
                if (m != missing) {
                    break;
                }
                i += Character.charCount(c);
            }
            String run = text.substring(start, i);
            Font f = missing ? fbFont : font;
            canvas.drawString(run, cx, y, f, paint);
            cx += f.measureTextWidth(run);
        }
    }

    /** 新画布（透明背景），对齐 Rust new_surface。 */
    public static Surface newSurface(int width, int height) {
        return Surface.makeRaster(io.github.humbleui.skija.ImageInfo.makeN32Premul(width, height));
    }
}
