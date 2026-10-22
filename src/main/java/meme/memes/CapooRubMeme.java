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

/** 移植自 memes/capoo_rub.rs。 */
@Component
public class CapooRubMeme extends AbstractMeme {

    public CapooRubMeme() {
        super(new MemeInfo("capoo_rub",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("咖波蹭", "咖波贴"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 23, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 23, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {178, 184, 78, 260}, {178, 174, 84, 269}, {178, 174, 84, 269}, {178, 178, 84, 264}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int[] loc = LOCS[i];  // (w, h, x, y)
            var frame = MemeResources.loadImage("capoo_rub/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(image, loc[0], loc[1]);
            canvas.drawImage(resized, loc[2], loc[3]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
