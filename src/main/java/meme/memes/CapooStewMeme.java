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

/** 移植自 memes/capoo_stew.rs。 */
@Component
public class CapooStewMeme extends AbstractMeme {

    public CapooStewMeme() {
        super(new MemeInfo("capoo_stew",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("咖波炖"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 10, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("capoo_stew/%d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var image = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 80, 80);
            int y = (outIdx == 2 || outIdx == 3 || outIdx == 5) ? 45 : 47;
            canvas.drawImage(image, 88, y);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 5, 0.08f);
    }
}
