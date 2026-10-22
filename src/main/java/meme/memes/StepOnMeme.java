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

/** 移植自 memes/step_on.rs。 */
@Component
public class StepOnMeme extends AbstractMeme {

    public StepOnMeme() {
        super(new MemeInfo("step_on",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("踩"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {104, 72, 32, 185, 25}, {104, 72, 32, 185, 25}, {90, 73, 51, 207, 0},
            {88, 78, 51, 202, 0}, {88, 86, 49, 197, 0}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            var frame = MemeResources.loadImage("step_on/%d.png".formatted(i));
            int[] p = PARAMS[i];  // (w, h, x, y, angle)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.rotate(ImageEdit.resizeExact(img, p[0], p[1]), p[4]);
            canvas.drawImage(resized, p[2], p[3]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.07f);
    }
}
