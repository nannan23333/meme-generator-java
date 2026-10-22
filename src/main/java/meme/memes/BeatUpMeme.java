package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
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

/** 移植自 memes/beat_up.rs。 */
@Component
public class BeatUpMeme extends AbstractMeme {

    public BeatUpMeme() {
        super(new MemeInfo("beat_up",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("揍"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 7, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] SELF_LOCS = {{100, 43}, {110, 46}, {101, 40}};
    private static final int[][] USER_LOCS = {{99, 136}, {99, 136}, {89, 140}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var selfHead = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 55, 55);
        var userHead = ImageEdit.resizeExact(ImageEdit.circle(images.get(1).image()), 45, 45);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var frame = MemeResources.loadImage("beat_up/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(selfHead, SELF_LOCS[i][0], SELF_LOCS[i][1]);
            canvas.drawImage(userHead, USER_LOCS[i][0], USER_LOCS[i][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
