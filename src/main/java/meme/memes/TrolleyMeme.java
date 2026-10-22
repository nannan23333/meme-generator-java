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

/** 移植自 memes/trolley.rs。 */
@Component
public class TrolleyMeme extends AbstractMeme {

    public TrolleyMeme() {
        super(new MemeInfo("trolley",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("推车"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS_0 = {{65, 65, 21, 101, 0}};
    private static final int[][] PARAMS_25 = {{65, 65, 0, 101, 0}, {65, 65, 0, 101, 0}, {65, 65, -21, 101, 0}};
    private static final int[][] PARAMS_31 = {
            {18, 18, 237, 25, 0}, {18, 18, 215, 25, 0}, {18, 18, 191, 25, 0}, {18, 18, 169, 25, 0},
            {18, 18, 150, 25, 0}, {18, 18, 129, 19, -20}, {18, 18, 114, 16, -30}, {18, 18, 92, 13, -40},
            {18, 18, 72, 9, -40}, {18, 18, 51, 6, -80}, {18, 18, 27, 7, -90}, {18, 18, 1, 8, -90},
            {18, 18, -15, 8, -90}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.circle(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            var frame = MemeResources.loadImage("trolley/%02d.png".formatted(i));
            int[] p;
            if (i < 25) {
                p = PARAMS_0[0];
            } else if (i < 28) {
                p = PARAMS_25[i - 25];
            } else if (i >= 31 && i <= 43) {
                p = PARAMS_31[i - 31];
            } else {
                frames.add(frame);
                continue;
            }
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var resized = ImageEdit.rotateCrop(ImageEdit.resizeExact(img, p[0], p[1]), p[4]);
            canvas.drawImage(resized, p[2], p[3]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
