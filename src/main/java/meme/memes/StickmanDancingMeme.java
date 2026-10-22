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

/** 移植自 memes/stickman_dancing.rs。 */
@Component
public class StickmanDancingMeme extends AbstractMeme {

    public StickmanDancingMeme() {
        super(new MemeInfo("stickman_dancing",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("跳舞", "火柴人跳舞"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 4, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 4, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {194, 53},
            {194, 53},
            {194, 52},
            {195, 52},
            {196, 52},
            {197, 52},
            {198, 51},
            {199, 51},
            {200, 51},
            {201, 51},
            {201, 52},
            {202, 53},
            {200, 53},
            {198, 54},
            {196, 54},
            {194, 54},
            {192, 55},
            {190, 55},
            {190, 54},
            {192, 53},
            {194, 53},
            {194, 52},
            {195, 52},
            {196, 52},
            {195, 52},
            {194, 52},
            {194, 53},
            {193, 53},
            {192, 53},
            {191, 53},
            {191, 54},
            {190, 54},
            {189, 54},
            {190, 54},
            {191, 54},
            {192, 53},
            {192, 53},
            {193, 53},
            {194, 53},
            {194, 52},
            {195, 52},
            {196, 52},
            {195, 52},
            {194, 52},
            {193, 53},
            {193, 53},
            {192, 53},
            {191, 54},
            {190, 54},
            {190, 54},
            {190, 54},
            {191, 54},
            {191, 53},
            {192, 53},
            {193, 53},
            {194, 53},
            {194, 52},
            {195, 52},
            {196, 52},
            {196, 52},
            {197, 52},
            {198, 51},
            {199, 51},
            {200, 51},
            {201, 51},
            {200, 52},
            {201, 52},
            {202, 53},
            {199, 53},
            {197, 54},
            {195, 54},
            {193, 54},
            {191, 55},
            {190, 54},
            {192, 53},
            {194, 53},
            {194, 52},
            {195, 52},
            {196, 52},
            {197, 52},
            {198, 51},
            {199, 51},
            {200, 51},
            {201, 51},
            {201, 52},
            {202, 53},
            {200, 53},
            {197, 54},
            {195, 54},
            {193, 54},
            {191, 55},
            {190, 54},
            {192, 53},
            {194, 53},
            {195, 52},
            {196, 52},
            {197, 52},
            {198, 51},
            {199, 51},
            {200, 51},
            {201, 52},
            {202, 53},
            {200, 53},
            {197, 54},
            {195, 54},
            {193, 54},
            {191, 55},
            {190, 54},
            {189, 58}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeExact(ImageEdit.circle(input), 138, 138);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 109; i++) {
            var frame = MemeResources.loadImage("stickman_dancing/%03d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(img, LOCS[i][0], LOCS[i][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.03f);
    }
}
