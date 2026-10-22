package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
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

/** osu osu：移植自 memes/osu.rs。 */
@Component
public class OsuMeme extends AbstractMeme {

    public OsuMeme() {
        super(new MemeInfo("osu",
                new MemeParams(0, 0, 1, 1, List.of("hso!"), List.of()),
                List.of("osu"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 7, 27, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 7, 27, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("osu/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(80, 80, 432, 432),
                text, 80f, 192f,
                meme.utils.TextRenderer.params().fontFamilies("Aller_Bd.ttf").fill(0xFFFFFFFF));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
