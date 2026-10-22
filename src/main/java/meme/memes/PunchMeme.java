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

/** 移植自 memes/punch.rs。 */
@Component
public class PunchMeme extends AbstractMeme {

    public PunchMeme() {
        super(new MemeInfo("punch",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("打拳"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {-50, 20}, {-40, 10}, {-30, 0}, {-20, -10}, {-10, -10}, {0, 0}, {10, 10},
            {20, 20}, {10, 10}, {0, 0}, {-10, -10}, {10, 0}, {-30, 10}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 260, 260);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            var frame = MemeResources.loadImage("punch/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(img, LOCS[i][0], LOCS[i][1] - 15);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.03f);
    }
}
