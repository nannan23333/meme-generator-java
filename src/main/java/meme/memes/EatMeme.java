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

/** eat 吃：移植自 memes/eat.rs。 */
@Component
public class EatMeme extends AbstractMeme {

    public EatMeme() {
        super(new MemeInfo("eat",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("吃"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 2, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var squared = ImageEdit.square(images.get(0).image());
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("eat/%d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeExact(squared, 34, 34);
            canvas.drawImage(image, 2, 38);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 3, 0.05f);
    }
}
