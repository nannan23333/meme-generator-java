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

/** ascension 升天：移植自 memes/ascension.rs。 */
@Component
public class AscensionMeme extends AbstractMeme {

    public AscensionMeme() {
        super(new MemeInfo("ascension",
                new MemeParams(0, 0, 1, 1, List.of("学的是机械"), List.of()),
                List.of("升天"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = "你原本应该要去地狱的，但因为你生前" + texts.get(0) + "，我们就当作你已经服完刑期了";
        var frame = MemeResources.loadImage("ascension/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(40, 30, 482, 135),
                text, 20f, 50f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
