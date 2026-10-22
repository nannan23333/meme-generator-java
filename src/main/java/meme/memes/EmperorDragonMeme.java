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

/** 移植自 memes/emperor_dragon.rs。 */
@Component
public class EmperorDragonMeme extends AbstractMeme {

    public EmperorDragonMeme() {
        super(new MemeInfo("emperor_dragon",
                new MemeParams(0, 0, 1, 1, List.of("听不懂，退朝"), List.of()),
                List.of("皇帝龙图"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 10, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 10, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("image_subtitle/emperor_dragon.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(10, frame.getHeight() - 70,
                        frame.getWidth() - 10, frame.getHeight() - 5),
                texts.get(0), 20.0f, 60.0f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
