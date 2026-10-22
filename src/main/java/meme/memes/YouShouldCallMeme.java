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

/** 移植自 memes/you_should_call.rs。 */
@Component
public class YouShouldCallMeme extends AbstractMeme {

    public YouShouldCallMeme() {
        super(new MemeInfo("you_should_call",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("致电", "你应该致电"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 27, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        String text = "这种情况你应该致电" + name;
        var frame = MemeResources.loadImage("you_should_call/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(50, 20, 700, 130),
                text, 20f, 70f, meme.utils.TextRenderer.params());
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 300, 300);
        canvas.drawImage(img, 400, 190);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
