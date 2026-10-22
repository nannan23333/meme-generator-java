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

/** 移植自 memes/roll.rs。 */
@Component
public class RollMeme extends AbstractMeme {

    public RollMeme() {
        super(new MemeInfo("roll",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("滚"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {87, 77, 0}, {96, 85, 45}, {92, 79, 90}, {92, 78, 135}, {92, 75, 180},
            {92, 75, 225}, {93, 76, 270}, {90, 80, 315}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 210, 210);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            var frame = MemeResources.loadImage("roll/%d.png".formatted(i));
            int[] loc = LOCS[i];  // (x, y, angle)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var rotated = ImageEdit.rotateCrop(img, loc[2]);
            canvas.drawImage(rotated, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
