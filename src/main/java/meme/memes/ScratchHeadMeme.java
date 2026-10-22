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

/** 移植自 memes/scratch_head.rs。 */
@Component
public class ScratchHeadMeme extends AbstractMeme {

    public ScratchHeadMeme() {
        super(new MemeInfo("scratch_head",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("挠头"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {53, 46, 4, 5}, {50, 45, 7, 6}, {50, 42, 6, 8}, {50, 44, 7, 7},
            {53, 42, 4, 8}, {52, 45, 7, 7}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            var frame = MemeResources.loadImage("scratch_head/%d.png".formatted(i));
            int[] loc = LOCS[i];  // (w, h, x, y)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(img, loc[0], loc[1]);
            canvas.drawImage(resized, loc[2], loc[3]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
