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

/** 移植自 memes/hammer.rs。 */
@Component
public class HammerMeme extends AbstractMeme {

    public HammerMeme() {
        super(new MemeInfo("hammer",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("锤"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {62, 143, 158, 113}, {52, 177, 173, 105}, {42, 192, 192, 92}, {46, 182, 184, 100},
            {54, 169, 174, 110}, {69, 128, 144, 135}, {65, 130, 152, 124}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            var frame = MemeResources.loadImage("hammer/%d.png".formatted(i));
            int[] loc = LOCS[i];  // (x, y, w, h)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(image, loc[2], loc[3]);
            canvas.drawImage(resized, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.07f);
    }
}
