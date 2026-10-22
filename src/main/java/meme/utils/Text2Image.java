package meme.utils;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本块：对齐 Rust Text2Image（from_text / from_bbcode_text）。
 * 语义：fromText 后即按最长行完成 layout；longestLine/height 可直接用；
 * layout(w) 重新按宽度换行；drawOnCanvas 先画描边段、再画填充段、最后下划线。
 */
public final class Text2Image {

    /** 文本段样式。 */
    static final class Seg {
        String text;
        int color = 0xFF000000;
        int strokeColor = 0xFF000000;
        float strokePx = 0f;
        boolean underline;

        Seg(String text) {
            this.text = text;
        }
    }

    /** 一行 = 若干 (seg 索引, 子串)。 */
    private record Piece(int segIdx, String text) {
    }

    private final List<Seg> segs = new ArrayList<>();
    private final List<List<Piece>> lines = new ArrayList<>();
    private final String fontFamily;
    /** 主字体缺字时的回退字体（fontFamily 写成 "主字体|回退字体" 时自动解析）。 */
    private final String fallbackFamily;
    private final float fontSize;
    private final float lineHeightRatio = 1.2f;
    /** 可选：用 Paint 直接填充/描边（如渐变 shader），对齐 Rust text_params!(paint=..., stroke_paint=...)。 */
    private float centerWidth = 0f;
    private Paint fillPaint;
    private Paint strokePaint;
    private float strokeWidthPx;

    private Text2Image(String fontFamily, float fontSize) {
        String primary = fontFamily;
        String fallback = null;
        if (fontFamily != null) {
            int sep = fontFamily.indexOf('|');
            if (sep < 0) {
                sep = fontFamily.indexOf(',');
            }
            if (sep > 0) {
                primary = fontFamily.substring(0, sep).trim();
                fallback = fontFamily.substring(sep + 1).trim();
            }
        }
        this.fontFamily = primary;
        this.fallbackFamily = fallback;
        this.fontSize = fontSize;
    }

    /** 回退字体（未指定则用全局默认回退）。 */
    private io.github.humbleui.skija.Typeface fallbackTypeface() {
        return fallbackFamily != null && !fallbackFamily.isEmpty()
                ? FontManager.typeface(fallbackFamily)
                : TextRenderer.fallbackTypeface();
    }

    /** 指定填充/描边 Paint（支持 shader 渐变）；strokePaint 为 null 表示不描边。 */
    public Text2Image withPaints(Paint fill, Paint stroke, float strokeWidthPx) {
        this.fillPaint = fill;
        this.strokePaint = stroke;
        this.strokeWidthPx = strokeWidthPx;
        return this;
    }

    /** 对齐 Rust Text2Image::from_text。fontFamily 为 null 用默认字体。 */
    public static Text2Image fromText(String text, float fontSize, String fontFamily,
                                      Integer color, Integer strokeColor, Float strokePx) {
        Text2Image t = new Text2Image(fontFamily, fontSize);
        Seg seg = new Seg(text);
        if (color != null) seg.color = color;
        if (strokeColor != null) seg.strokeColor = strokeColor;
        if (strokePx != null) seg.strokePx = strokePx;
        t.segs.add(seg);
        t.wrap();
        return t;
    }

    /** 对齐 Rust Text2Image::from_bbcode_text，支持 [color=x] [stroke=x] [u] 嵌套标签。 */
    public static Text2Image fromBbcode(String bbcode, float fontSize, String fontFamily,
                                        Integer color, Integer strokeColor, Float strokePx) {
        Text2Image t = new Text2Image(fontFamily, fontSize);
        // 解析：维护样式栈，把文本切成带样式的段
        int curColor = color != null ? color : 0xFF000000;
        int curStroke = strokeColor != null ? strokeColor : 0xFF000000;
        float curStrokePx = strokePx != null ? strokePx : 0f;
        boolean curUnderline = false;
        StringBuilder buf = new StringBuilder();

        record Style(int color, int stroke, float strokePx, boolean underline) {
        }
        List<Style> stack = new ArrayList<>();
        Style base = new Style(curColor, curStroke, curStrokePx, curUnderline);
        stack.add(base);

        Pattern tag = Pattern.compile("\\[(/?)(\\w+)(?:=([^\\]]*))?]");
        Matcher m = tag.matcher(bbcode);
        int pos = 0;
        while (m.find()) {
            if (m.start() > pos) {
                String chunk = bbcode.substring(pos, m.start());
                if (!chunk.isEmpty()) {
                    Seg seg = new Seg(chunk);
                    Style s = stack.get(stack.size() - 1);
                    seg.color = s.color;
                    seg.strokeColor = s.stroke;
                    seg.strokePx = s.strokePx;
                    seg.underline = s.underline;
                    t.segs.add(seg);
                }
            }
            boolean close = m.group(1).equals("/");
            String name = m.group(2).toLowerCase();
            String val = m.group(3);
            if (close) {
                if (stack.size() > 1) stack.remove(stack.size() - 1);
            } else {
                Style cur = stack.get(stack.size() - 1);
                int nc = cur.color, ns = cur.stroke;
                float np = cur.strokePx;
                boolean nu = cur.underline;
                switch (name) {
                    case "color" -> nc = val != null ? parseColor(val, cur.color) : cur.color;
                    case "stroke" -> {
                        if (val != null) {
                            ns = parseColor(val, cur.stroke);
                            if (np == 0) np = fontSize * 0.08f;
                        }
                    }
                    case "u" -> nu = true;
                    default -> {
                    }
                }
                stack.add(new Style(nc, ns, np, nu));
            }
            pos = m.end();
        }
        if (pos < bbcode.length()) {
            Seg seg = new Seg(bbcode.substring(pos));
            Style s = stack.get(stack.size() - 1);
            seg.color = s.color;
            seg.strokeColor = s.stroke;
            seg.strokePx = s.strokePx;
            seg.underline = s.underline;
            t.segs.add(seg);
        }
        t.wrap();
        return t;
    }

    private static int parseColor(String val, int fallback) {
        try {
            String v = val.trim();
            if (v.startsWith("#")) v = v.substring(1);
            if (v.length() == 3) {
                v = "" + v.charAt(0) + v.charAt(0) + v.charAt(1) + v.charAt(1) + v.charAt(2) + v.charAt(2);
            }
            return 0xFF000000 | (int) Long.parseLong(v, 16);
        } catch (Exception e) {
            return fallback;
        }
    }

    /** 主字体（已解析字体名），供调用方读取字体度量。 */
    public Font resolvedFont() {
        return font();
    }

    private Font font() {
        return fontFamily != null
                ? FontManager.font(fontFamily, fontSize)
                : new Font(FontManager.defaultTypeface(), fontSize);
    }

    /** 按 \n 与段边界做贪婪换行（首次按最长行，后续 layout(width) 调用）。 */
    private void wrap() {
        float longest = longestLineWidth();
        // 余量：分段宽度之和与整体测量存在亚像素差异，加 5% 避免边缘误换行
        layout(longest * 1.05f + 4f);
    }

    /** 以指定宽度重新换行。 */
    public void layout(float width) {
        lines.clear();
        if (width <= 0) width = 1;
        final float w = width;
        Font font = font();
        // 段内先按 \n 切分，再贪婪吸收
        List<int[]> pending = new ArrayList<>(); // segIdx -> char positions
        List<Piece> current = new ArrayList<>();
        StringBuilder currentText = new StringBuilder();
        for (int si = 0; si < segs.size(); si++) {
            String text = segs.get(si).text;
            int i = 0;
            while (i < text.length()) {
                int nl = text.indexOf('\n', i);
                String chunk = nl >= 0 ? text.substring(i, nl) : text.substring(i);
                if (!chunk.isEmpty()) {
                    // 逐码点贪婪
                    for (int ci = 0; ci < chunk.length(); ) {
                        int cp = chunk.codePointAt(ci);
                        int len = Character.charCount(cp);
                        String ch = chunk.substring(ci, ci + len);
                        String cand = currentText + ch;
                        if (currentText.length() > 0 && TextRenderer.measureAuto(font, cand, fallbackTypeface()) > w) {
                            lines.add(current);
                            current = new ArrayList<>();
                            currentText.setLength(0);
                        }
                        if (current.isEmpty() || current.get(current.size() - 1).segIdx != si) {
                            current.add(new Piece(si, ch));
                        } else {
                            Piece last = current.remove(current.size() - 1);
                            current.add(new Piece(si, last.text + ch));
                        }
                        currentText.append(ch);
                        ci += len;
                    }
                }
                if (nl >= 0) {
                    lines.add(current);
                    current = new ArrayList<>();
                    currentText.setLength(0);
                    i = nl + 1;
                } else {
                    i = text.length();
                }
            }
        }
        lines.add(current);
    }

    private float measure(Font font, String text) {
        return TextRenderer.width(font, text);
    }

    /** 当前布局下最长行宽。 */
    public float longestLine() {
        return longestLineWidth();
    }

    private float longestLineWidth() {
        Font font = font();
        float max = 0;
        for (List<Piece> line : lines.isEmpty() ? preLines() : lines) {
            // 整行拼接测量（含字距），与 layout 的逐字累积测量口径一致，
            // 避免"分段之和 < 整串宽度"导致 wrap 宽度不足而误换行
            StringBuilder sb = new StringBuilder();
            for (Piece p : line) sb.append(p.text);
            max = Math.max(max, TextRenderer.measureAuto(font, sb.toString(), fallbackTypeface()));
        }
        return max;
    }

    /** 未换行时的行（按 \n 切）。 */
    private List<List<Piece>> preLines() {
        List<List<Piece>> out = new ArrayList<>();
        List<Piece> cur = new ArrayList<>();
        for (int si = 0; si < segs.size(); si++) {
            String text = segs.get(si).text;
            int i = 0;
            while (i < text.length()) {
                int nl = text.indexOf('\n', i);
                String chunk = nl >= 0 ? text.substring(i, nl) : text.substring(i);
                if (!chunk.isEmpty()) cur.add(new Piece(si, chunk));
                if (nl >= 0) {
                    out.add(cur);
                    cur = new ArrayList<>();
                    i = nl + 1;
                } else {
                    i = text.length();
                }
            }
        }
        out.add(cur);
        return out;
    }

    /** 当前布局下总高（行数 × 行高）。 */
    public float height() {
        int n = Math.max(1, lines.size());
        return n * fontSize * lineHeightRatio;
    }

    /**
     * 对齐 Rust <code>draw_bbcode_text_area</code>：按框宽 layout，垂直居中绘制。
     * Rust 的 TextParams 默认 text_align = Center，所以每行还要在框宽内水平居中。
     */
    public static void drawBbcodeArea(Canvas canvas, io.github.humbleui.types.Rect box,
                                      String bbcode, float fontSize, String fontFamily) {
        Text2Image t = fromBbcode(bbcode, fontSize, fontFamily, null, null, null);
        t.layout(box.getWidth());
        float top = box.getTop() + (box.getHeight() - t.height()) / 2f;
        t.centerWidth = box.getWidth();
        t.drawOnCanvas(canvas, box.getLeft(), top);
    }

    /**
     * 对齐 Rust <code>draw_bbcode_text_area_auto_font_size</code>：
     * 从 maxSize 逐级降 1，直到按框宽排版后高度放得下，再垂直居中绘制；都不行抛 551。
     */
    public static void drawBbcodeAreaAutoFontSize(Canvas canvas, io.github.humbleui.types.Rect box,
                                                  String bbcode, float minSize, float maxSize,
                                                  String fontFamily) {
        for (float size = maxSize; size >= minSize - 0.001f; size -= 1f) {
            Text2Image t = fromBbcode(bbcode, size, fontFamily, null, null, null);
            t.layout(box.getWidth());
            if (t.height() <= box.getHeight()) {
                float top = box.getTop() + (box.getHeight() - t.height()) / 2f;
                t.centerWidth = box.getWidth();
                t.drawOnCanvas(canvas, box.getLeft(), top);
                return;
            }
        }
        throw meme.core.MemeError.textOverLength(bbcode);
    }

    /** 在 (x, y)（左上角）绘制。 */
    public void drawOnCanvas(Canvas canvas, float x, float y) {
        Font font = font();
        float lineH = fontSize * lineHeightRatio;
        float yy = y;
        for (List<Piece> line : lines) {
            // 水平居中（centerWidth>0 时）：对齐 Rust TextParams 默认 text_align=Center
            float x0 = x;
            if (centerWidth > 0) {
                StringBuilder sb = new StringBuilder();
                for (Piece p : line) {
                    sb.append(p.text);
                }
                x0 = x + Math.max(0f, (centerWidth - TextRenderer.measureAuto(font, sb.toString(), fallbackTypeface())) / 2f);
            }
            float xx = x0;
            // 描边层
            for (Piece p : line) {
                Seg seg = segs.get(p.segIdx);
                if (strokePaint != null) {
                    // 外部传入的 Paint（可能带渐变 shader），由调用方决定样式
                    TextRenderer.drawStringAuto(canvas, p.text, xx, yy + fontSize * 0.9f, font, strokePaint, fallbackTypeface());
                } else if (seg.strokePx > 0) {
                    try (Paint stroke = new Paint()
                            .setMode(io.github.humbleui.skija.PaintMode.STROKE)
                            .setStrokeWidth(seg.strokePx)
                            .setAntiAlias(true)
                            .setColor(seg.strokeColor)) {
                        TextRenderer.drawStringAuto(canvas, p.text, xx, yy + fontSize * 0.9f, font, stroke, fallbackTypeface());
                    }
                }
                xx += TextRenderer.measureAuto(font, p.text, fallbackTypeface());
            }
            // 填充层
            xx = x0;
            for (Piece p : line) {
                Seg seg = segs.get(p.segIdx);
                if (fillPaint != null) {
                    TextRenderer.drawStringAuto(canvas, p.text, xx, yy + fontSize * 0.9f, font, fillPaint, fallbackTypeface());
                } else {
                    try (Paint fill = new Paint().setAntiAlias(true).setColor(seg.color)) {
                        TextRenderer.drawStringAuto(canvas, p.text, xx, yy + fontSize * 0.9f, font, fill, fallbackTypeface());
                    }
                }
                xx += TextRenderer.measureAuto(font, p.text, fallbackTypeface());
            }
            // 下划线层
            xx = x0;
            for (Piece p : line) {
                Seg seg = segs.get(p.segIdx);
                if (seg.underline) {
                    try (Paint ul = new Paint().setAntiAlias(true).setColor(seg.color)) {
                        float ulY = yy + fontSize * 1.02f;
                        canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(
                                xx, ulY, TextRenderer.measureAuto(font, p.text, fallbackTypeface()), Math.max(1f, fontSize / 18f)), ul);
                    }
                }
                xx += TextRenderer.measureAuto(font, p.text, fallbackTypeface());
            }
            yy += lineH;
        }
    }
}
