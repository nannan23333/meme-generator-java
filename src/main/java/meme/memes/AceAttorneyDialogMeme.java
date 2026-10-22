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

/** 移植自 memes/ace_attorney_dialog.rs。 */
@Component
public class AceAttorneyDialogMeme extends AbstractMeme {

    public AceAttorneyDialogMeme() {
        super(new MemeInfo("ace_attorney_dialog",
                new MemeParams(0, 0, 1, 1, List.of("表情包制作"), List.of()),
                List.of("逆转裁判气泡"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 3, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 18, 0, 0), ZoneOffset.ofHours(8))));
    }

    /** 带阴影的字符（对齐 Rust ShadowText）。 */
    private record ShadowText(Text2Image inner, Text2Image shadow, float shadowWidth) {
        float width() {
            return inner.longestLine();
        }

        float height() {
            return inner.height();
        }

        void drawOnCanvas(io.github.humbleui.skija.Canvas canvas, float x, float y) {
            shadow.drawOnCanvas(canvas, x + shadowWidth * 0.25f, y + shadowWidth);
            inner.drawOnCanvas(canvas, x, y);
        }
    }

    private ShadowText shadowText(String text, float fontsize, float shadowWidth) {
        var inner = Text2Image.fromText(text, fontsize, "PangMenZhengDao-Cu.ttf",
                0xFFE60012, 0xFF500000, 8f);
        var shadow = Text2Image.fromText(text, fontsize, "PangMenZhengDao-Cu.ttf",
                0xFF500000, 0xFF500000, shadowWidth);
        return new ShadowText(inner, shadow, shadowWidth);
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        float ratio = 0.6f;
        String text = texts.get(0);
        float fontSize = 650f;
        float shadowWidth = 20f;
        float maxWidth = 900f;
        float minFontSize = 100f;
        java.util.List<ShadowText> shadowTexts;
        while (true) {
            final float fs = fontSize;
            final float sw = shadowWidth;
            shadowTexts = text.codePoints()
                    .mapToObj(c -> shadowText(new String(Character.toChars(c)), fs, sw))
                    .collect(java.util.stream.Collectors.toList());
            float totalW = shadowTexts.get(0).width()
                    + (float) shadowTexts.stream().skip(1).mapToDouble(t -> t.width() * ratio).sum();
            if (totalW <= maxWidth) break;
            if (fontSize <= minFontSize) {
                throw meme.core.MemeError.textOverLength(text);
            }
            fontSize -= 10f;
            shadowWidth -= 0.3f;
        }
        float textsWidth = shadowTexts.get(0).width()
                + (float) shadowTexts.stream().skip(1).mapToDouble(t -> t.width() * ratio).sum();
        float textsHeight = shadowTexts.stream().map(ShadowText::height)
                .max(Float::compare).orElse(0f);

        var frame = MemeResources.loadImage("ace_attorney_dialog/bubble.png");
        var mark0 = MemeResources.loadImage("ace_attorney_dialog/mark.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.save();
        canvas.translate(frame.getWidth() / 2f, frame.getHeight() / 2f);
        canvas.rotate(-10f);
        float x = -textsWidth / 2f;
        float y = -textsHeight / 2f;
        for (ShadowText t : shadowTexts) {
            t.drawOnCanvas(canvas, x, y);
            x += t.width() * ratio;
        }
        float markHeight = fontSize * 1.2f;
        float markY = y + textsHeight - markHeight;
        var mark = ImageEdit.resizeHeight(mark0, Math.round(markHeight));
        canvas.drawImage(mark, x, markY);
        canvas.restore();
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
