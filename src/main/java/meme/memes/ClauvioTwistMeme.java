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

/** 移植自 memes/clauvio_twist.rs。 */
@Component
public class ClauvioTwistMeme extends AbstractMeme {

    public ClauvioTwistMeme() {
        super(new MemeInfo("clauvio_twist",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("鼠鼠搓"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 10, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 10, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {0, 45, 144}, {0, 45, 144}, {0, 45, 144}, {2, 45, 141}, {4, 45, 141}, {8, 40, 141},
            {12, 38, 142}, {30, 32, 148}, {75, 25, 158}, {115, 0, 160}, {130, 0, 160},
            {125, 0, 155}, {120, 0, 150}, {115, 0, 148}, {110, 5, 146}, {85, 14, 146},
            {70, 19, 146}, {45, 28, 144}, {37, 38, 141}, {10, 42, 144}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("clauvio_twist/%02d.png".formatted(outIdx));
            int[] p = PARAMS[outIdx % 20];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.rotateCrop(
                    ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 100, 100), -p[0]);
            canvas.drawImage(image, p[1], p[2]);
            return surface.makeImageSnapshot();
        }, 40, 0.05f);
    }
}
