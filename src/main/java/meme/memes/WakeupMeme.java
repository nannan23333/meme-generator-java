package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/wakeup.rs。 */
@Component
public class WakeupMeme extends AbstractMeme {

    public WakeupMeme() {
        super(new MemeInfo("wakeup",
                new MemeParams(0, 0, 1, 1, List.of("好"), List.of()),
                List.of("好起来了"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 20, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("wakeup/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(310, 270, 460, 380),
                text, 50f, 90f, meme.utils.TextRenderer.params());
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(50, 610, 670, 720),
                text + "起来了", 70f, 110f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
