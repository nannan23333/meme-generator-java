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

/** 移植自 memes/charpic.rs。 */
@Component
public class CharpicMeme extends AbstractMeme {

    public CharpicMeme() {
        super(new MemeInfo("charpic",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("字符画"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 7, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 1, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String STR_MAP = "@@$$&B88QMMGW##EE93SPPDOOU**==()+^,\"--''.  ";

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var probe = Text2Image.fromText("@", 15f, "consola.ttf", null, null, null);
        float ratio = probe.longestLine() / probe.height();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img0 = ImageEdit.resizeWidth(input, 150);
            int newH = Math.round(img0.getHeight() * ratio);
            var img = ImageEdit.resizeExact(img0, img0.getWidth(), Math.max(1, newH));
            var bi = ImageEdit.toBufferedImage(img);
            List<String> lines = new ArrayList<>();
            for (int y = 0; y < bi.getHeight(); y++) {
                StringBuilder line = new StringBuilder();
                for (int x = 0; x < bi.getWidth(); x++) {
                    int rgb = bi.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                    int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                    int idx = Math.min(gray * STR_MAP.length() / 256, STR_MAP.length() - 1);
                    line.append(STR_MAP.charAt(idx));
                }
                lines.add(line.toString());
            }
            String text = String.join("\n", lines);
            var textImg = Text2Image.fromText(text, 15f, "consola.ttf", null, null, null);
            int frameW = Math.round((float) Math.ceil(textImg.longestLine()));
            int frameH = Math.round((float) Math.ceil(textImg.height()));
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            textImg.drawOnCanvas(canvas, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
