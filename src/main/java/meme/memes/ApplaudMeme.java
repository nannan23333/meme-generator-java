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

/** applaud 鼓掌：移植自 memes/applaud.rs。 */
@Component
public class ApplaudMeme extends AbstractMeme {

    public ApplaudMeme() {
        super(new MemeInfo("applaud",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("鼓掌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 8, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {109, 102, 27, 17}, {107, 105, 28, 15}, {110, 106, 27, 14},
            {109, 106, 27, 14}, {107, 108, 29, 12},
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("applaud/%d.png".formatted(outIdx));
            int[] loc = LOCS[outIdx];
            var image = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), loc[0], loc[1]);
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(image, loc[2], loc[3]);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 5, 0.1f);
    }
}
