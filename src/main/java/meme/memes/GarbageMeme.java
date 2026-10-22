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

/** 移植自 memes/garbage.rs。 */
@Component
public class GarbageMeme extends AbstractMeme {

    public GarbageMeme() {
        super(new MemeInfo("garbage",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("垃圾", "垃圾桶"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {39, 40}, {39, 40}, {39, 30}, {39, 30}, {39, 32}, {39, 32}, {39, 32}, {39, 32},
            {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 30}, {39, 27},
            {39, 32}, {37, 49}, {37, 64}, {37, 67}, {37, 67}, {39, 69}, {37, 70}, {37, 70}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 79, 79);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            var frame = MemeResources.loadImage("garbage/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            int[] loc = LOCS[Math.min(i, LOCS.length - 1)];
            canvas.drawImage(image, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.04f);
    }
}
