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

/** 移植自 memes/universal.rs。 */
@Component
public class UniversalMeme extends AbstractMeme {

    public UniversalMeme() {
        super(new MemeInfo("universal",
                new MemeParams(1, 1, 1, 1, List.of(), List.of(new MemeOption.IntegerOption("size", 50, 20, 100, null, meme.core.ParserFlags.none()), new MemeOption.StringOption("font", null, null, null, meme.core.ParserFlags.none()), new MemeOption.StringOption("color", null, null, null, meme.core.ParserFlags.none()), new MemeOption.StringOption("position", "bottom_outer", List.of("top", "bottom", "center", "top_outer", "bottom_outer"), null, meme.core.ParserFlags.none()), new MemeOption.BooleanOption("bold", false, null, meme.core.ParserFlags.none()), new MemeOption.BooleanOption("italic", false, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("rotate", 0, -180, 180, null, meme.core.ParserFlags.none()), new MemeOption.StringOption("align", "center", List.of("left", "center", "right"), null, meme.core.ParserFlags.none()), new MemeOption.FloatOption("stroke_width", null, 0f, 20f, null, meme.core.ParserFlags.none()), new MemeOption.StringOption("stroke_color", null, null, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("x_offset", 0, null, null, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("y_offset", 0, null, null, null, meme.core.ParserFlags.none()))),
                List.of("万能表情", "空白表情"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 13, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        int fontSize = intOption(options, "size", 50);
        String position = stringOption(options, "position", "bottom_outer");
        boolean bold = boolOption(options, "bold", false);
        String font = stringOption(options, "font", null);
        String fontFile = font != null ? font : (bold ? "NotoSansSC-Bold.ttf" : null);
        int color = 0xFF000000;
        Integer strokeColor = null;
        Float strokeWidth = null;
        if (position.equals("top") || position.equals("bottom") || position.equals("center")) {
            color = 0xFFFFFFFF;
            strokeColor = 0xFF000000;
            strokeWidth = fontSize / 10f;
        }
        String colorOpt = stringOption(options, "color", null);
        if (colorOpt != null) {
            color = parseHexColor(colorOpt, color);
        }
        String strokeColorOpt = stringOption(options, "stroke_color", null);
        if (strokeColorOpt != null) {
            strokeColor = parseHexColor(strokeColorOpt, 0xFF000000);
        }
        Object sw = options.get("stroke_width");
        if (sw instanceof Number swn) {
            strokeWidth = swn.floatValue();
        }
        if (strokeColor != null && strokeWidth == null) {
            strokeWidth = fontSize / 10f;
        }
        var textImg = Text2Image.fromText(text, fontSize, fontFile,
                color, strokeColor, strokeWidth);
        int textW = Math.round(textImg.longestLine());
        int textH = Math.round(textImg.height());
        int padding = 10;
        int rotate = intOption(options, "rotate", 0);
        int xOffset = intOption(options, "x_offset", 0);
        int yOffset = intOption(options, "y_offset", 0);

        return InputImage.makePngOrGif(images.get(0), input -> {
            int imgW = 500;
            var img = ImageEdit.resizeWidth(input, imgW);
            int imgH = img.getHeight();
            int frameW = imgW;
            int frameH;
            int imgY;
            int textY;
            switch (position) {
                case "top" -> {
                    frameH = imgH; imgY = 0; textY = 0;
                }
                case "bottom" -> {
                    frameH = imgH; imgY = 0; textY = imgH - textH - padding;
                }
                case "center" -> {
                    frameH = imgH; imgY = 0; textY = (imgH - textH) / 2;
                }
                case "top_outer" -> {
                    frameH = imgH + textH + padding; imgY = textH + padding; textY = 0;
                }
                default -> {
                    frameH = imgH + textH + padding; imgY = 0; textY = imgH;
                }
            }
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, 0, imgY);
            int centerX = frameW / 2 + xOffset;
            int centerY = textY + textH / 2 + yOffset;
            canvas.save();
            canvas.translate(centerX, centerY);
            canvas.rotate(rotate);
            textImg.drawOnCanvas(canvas, -textW / 2f, -textH / 2f);
            canvas.restore();
            return surface.makeImageSnapshot();
        });
    }

    private static int parseHexColor(String v, int fallback) {
        try {
            String s = v.startsWith("#") ? v.substring(1) : v;
            return 0xFF000000 | (int) Long.parseLong(s, 16);
        } catch (Exception e) {
            return fallback;
        }
    }
}
