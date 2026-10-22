package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/p5letter.rs。 */
@Component
public class P5letterMeme extends AbstractMeme {

    public P5letterMeme() {
        super(new MemeInfo("p5letter",
                new MemeParams(0, 0, 1, 1, List.of("TAKEYOURHEART"), List.of()),
                List.of("p5预告信"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 21, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int COLOR_RED = 0xFFE5191C;
    private static final int COLOR_WHITE = 0xFFFDFDFD;
    private static final int COLOR_BLACK = 0xFF0F0F0F;

    private record BoxChar(io.github.humbleui.skija.Image image, int width, int outerWidth, int outerHeight) {
    }

    private BoxChar newBoxChar(String ch, int mode, float fontSize) {
        float angle = (float) (-Math.random() * 10);
        if (mode != 0) {
            angle = angle * (Math.random() < 0.5 ? -1 : 1);
        }
        float scale = mode == 0 ? 1.1f : 1f - (int) (Math.random() * 3) / 10f;
        float fs = fontSize * scale;
        int color = mode == 2 ? COLOR_RED : COLOR_WHITE;
        var t2i = Text2Image.fromText(ch, fs, "NotoSansSC-Bold.ttf", color, null, null);
        float textW = t2i.longestLine();
        float textH = t2i.height();
        int bgColor = mode == 0 ? COLOR_RED : COLOR_BLACK;
        int bgW = Math.round(textW * 1.4f);
        int bgH = Math.round(textH * 1.1f);
        var bs = meme.utils.TextRenderer.newSurface(Math.max(1, bgW), Math.max(1, bgH));
        bs.getCanvas().clear(bgColor);
        var bg = bs.makeImageSnapshot();

        if (mode == 0) {
            float extraScale = 1.2f;
            int ebw = Math.round(bgW * extraScale);
            int ebh = Math.round(bgH * extraScale);
            var es = meme.utils.TextRenderer.newSurface(ebw, ebh);
            var ec = es.getCanvas();
            ec.clear(COLOR_BLACK);
            float extraAngle = (float) (Math.random() * 5) * (Math.random() < 0.5 ? -1 : 1);
            var rotated = ImageEdit.rotate(bg, extraAngle);
            ec.drawImage(rotated, (ebw - rotated.getWidth()) / 2, (ebh - rotated.getHeight()) / 2);
            bg = es.makeImageSnapshot();
        }

        int borderSize = 6;
        int bw = bg.getWidth() + borderSize * 2;
        int bh = bg.getHeight() + borderSize * 2;
        var surface = meme.utils.TextRenderer.newSurface(bw, bh);
        var canvas = surface.getCanvas();
        canvas.clear(COLOR_WHITE);
        canvas.drawImage(bg, borderSize, borderSize);
        t2i.drawOnCanvas(canvas, (bw - t2i.longestLine()) / 2f, (bh - t2i.height()) / 2f);
        var border = surface.makeImageSnapshot();
        var image = ImageEdit.rotate(border, angle);
        return new BoxChar(image, border.getWidth(), image.getWidth(), image.getHeight());
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        List<List<BoxChar>> boxLinesChars = new ArrayList<>();
        for (String line : text.split("\n", -1)) {
            List<BoxChar> boxChars = new ArrayList<>();
            for (int i = 0; i < line.length(); ) {
                int cp = line.codePointAt(i);
                String ch = new String(Character.toChars(cp));
                int mode;
                if (i == 0) {
                    mode = 0; // First
                } else if (i % 2 == 0) {
                    mode = 2; // Red
                } else {
                    mode = 1; // White
                }
                boxChars.add(newBoxChar(ch, mode, 120f));
                i += Character.charCount(cp);
            }
            if (boxChars.isEmpty()) continue;
            int width = boxChars.stream().mapToInt(BoxChar::width).sum();
            width += (boxChars.get(0).outerWidth() - boxChars.get(0).width()) / 2;
            width += (boxChars.get(boxChars.size() - 1).outerWidth() - boxChars.get(boxChars.size() - 1).width()) / 2;
            if (width > 1700) {
                throw meme.core.MemeError.textOverLength(text);
            }
            boxLinesChars.add(boxChars);
        }
        // 组行
        record BoxLine(List<BoxChar> chars, int width, int height) {
        }
        List<BoxLine> boxLines = new ArrayList<>();
        for (List<BoxChar> chars : boxLinesChars) {
            int width = chars.stream().mapToInt(BoxChar::width).sum();
            width += (chars.get(0).outerWidth() - chars.get(0).width()) / 2;
            width += (chars.get(chars.size() - 1).outerWidth() - chars.get(chars.size() - 1).width()) / 2;
            int height = chars.stream().mapToInt(BoxChar::outerHeight).max().orElse(0);
            boxLines.add(new BoxLine(chars, width, height));
        }

        var frame = MemeResources.loadImage("p5letter/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        int totalHeight = boxLines.stream().mapToInt(BoxLine::height).sum();
        int y = (frame.getHeight() - totalHeight) / 2;
        for (BoxLine line : boxLines) {
            int x = (frame.getWidth() - line.width()) / 2;
            int dx = (line.chars().get(0).outerWidth() - line.chars().get(0).width()) / 2;
            int xx = x + dx;
            for (BoxChar bc : line.chars()) {
                canvas.drawImage(bc.image(),
                        xx - (bc.outerWidth() - bc.width()) / 2,
                        y + (line.height() - bc.outerHeight()) / 2);
                xx += bc.width();
            }
            y += line.height();
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
