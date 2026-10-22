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

/** thump 捶：移植自 memes/thump.rs。 */
@Component
public class ThumpMeme extends AbstractMeme {

    public ThumpMeme() {
        super(new MemeInfo("thump",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("捶"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {65, 128, 77, 72}, {67, 128, 73, 72},
            {54, 139, 94, 61}, {57, 135, 86, 65},
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var squared = ImageEdit.square(images.get(0).image());
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("thump/%d.png".formatted(outIdx));
            int[] p = PARAMS[outIdx];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeExact(squared, p[2], p[3]);
            canvas.drawImage(img, p[0], p[1]);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 4, 0.04f);
    }
}
