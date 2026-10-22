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

/** 移植自 memes/rise_dead.rs。 */
@Component
public class RiseDeadMeme extends AbstractMeme {

    public RiseDeadMeme() {
        super(new MemeInfo("rise_dead",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("诈尸", "秽土转生"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {{81, 55}, {74, 49}, {-66, 36}, {-231, 55}};
    private static final int[][][] POINTS = {
            {{0, 2}, {101, 0}, {103, 105}, {1, 105}},
            {{0, 3}, {104, 0}, {106, 108}, {1, 108}},
            {{0, 0}, {182, 5}, {184, 194}, {1, 185}},
            {{0, 0}, {259, 4}, {276, 281}, {13, 278}}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 150, 150);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 34; i++) {
            var frame = MemeResources.loadImage("rise_dead/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            if (i <= 28) {
                int idx = i <= 25 ? 0 : i - 25;
                int x = LOCS[idx][0];
                int y = LOCS[idx][1];
                if (i % 2 == 1) { x += 1; y -= 1; }
                int[][] p = POINTS[idx];
                var persp = ImageEdit.perspective(img, p[0][0], p[0][1], p[1][0], p[1][1], p[2][0], p[2][1], p[3][0], p[3][1]);
                canvas.drawImage(persp, x, y);
            }
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.15f);
    }
}
