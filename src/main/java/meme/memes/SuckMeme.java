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

/** 移植自 memes/suck.rs。 */
@Component
public class SuckMeme extends AbstractMeme {

    public SuckMeme() {
        super(new MemeInfo("suck",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("吸", "嗦"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 5, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {82, 100, 130, 119}, {82, 94, 126, 125}, {82, 120, 128, 99}, {81, 164, 132, 55},
            {79, 163, 132, 55}, {82, 140, 127, 79}, {83, 152, 125, 67}, {75, 157, 140, 62},
            {72, 165, 144, 54}, {80, 132, 128, 87}, {81, 127, 127, 92}, {79, 111, 132, 108}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            var frame = MemeResources.loadImage("suck/%02d.png".formatted(i));
            int[] loc = LOCS[i];  // (x, y, w, h)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(img, loc[2], loc[3]);
            canvas.drawImage(resized, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
