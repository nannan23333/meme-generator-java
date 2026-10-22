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

/** bad_news 悲报：移植自 memes/bad_news.rs。 */
@Component
public class BadNewsMeme extends AbstractMeme {

    public BadNewsMeme() {
        super(new MemeInfo("bad_news",
                new MemeParams(0, 0, 1, 1, List.of("喜报"), List.of()),
                List.of("悲报"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("bad_news/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(50, 100, frame.getWidth() - 50, frame.getHeight() - 100),
                text, 30f, 60f,
                meme.utils.TextRenderer.params().stroke(0.083f, 0xFFFFFFFF));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
