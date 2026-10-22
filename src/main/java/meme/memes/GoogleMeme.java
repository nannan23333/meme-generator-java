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

/** 移植自 memes/google.rs。 */
@Component
public class GoogleMeme extends AbstractMeme {

    public GoogleMeme() {
        super(new MemeInfo("google",
                new MemeParams(0, 0, 1, 1, List.of("Google"), List.of()),
                List.of("google"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        String[] colors = {"#4285f4", "#db4437", "#f4b400", "#4285f4", "#0f9d58", "#db4437"};
        StringBuilder bb = new StringBuilder();
        int[] cps = text.codePoints().toArray();
        for (int i = 0; i < cps.length; i++) {
            String ch = new String(Character.toChars(cps[i]));
            if (Character.isWhitespace(cps[i])) {
                bb.append(ch);
            } else {
                bb.append("[color=").append(colors[i % colors.length]).append("]")
                        .append(ch).append("[/color]");
            }
        }
        var textImg = Text2Image.fromBbcode(bb.toString(), 200f, null, null, null, null);
        int w = Math.round(textImg.longestLine()) + 100;
        int h = Math.round(textImg.height()) + 100;
        var surface = meme.utils.TextRenderer.newSurface(w, h);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        textImg.drawOnCanvas(canvas, 50, 50);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
