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

/** 移植自 memes/pound.rs。 */
@Component
public class PoundMeme extends AbstractMeme {

    public PoundMeme() {
        super(new MemeInfo("pound",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("捣"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {135, 240, 138, 47}, {135, 240, 138, 47}, {150, 190, 105, 95}, {150, 190, 105, 95},
            {148, 188, 106, 98}, {146, 196, 110, 88}, {145, 223, 112, 61}, {145, 223, 112, 61}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            var frame = MemeResources.loadImage("pound/%d.png".formatted(i));
            int[] loc = LOCS[i];  // (x, y, w, h)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(img, loc[2], loc[3]);
            canvas.drawImage(resized, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
