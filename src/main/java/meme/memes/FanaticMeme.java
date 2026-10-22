package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/fanatic.rs。 */
@Component
public class FanaticMeme extends AbstractMeme {

    public FanaticMeme() {
        super(new MemeInfo("fanatic",
                new MemeParams(0, 0, 1, 1, List.of("洛天依") , List.of()),
                List.of("狂爱", "狂粉"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 2, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 2, 5, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("fanatic/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(70, 150, 250, 220),
                text, 20f, 50f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
