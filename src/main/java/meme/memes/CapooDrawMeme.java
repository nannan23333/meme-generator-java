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

/** 移植自 memes/capoo_draw.rs。 */
@Component
public class CapooDrawMeme extends AbstractMeme {

    public CapooDrawMeme() {
        super(new MemeInfo("capoo_draw",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("咖波画"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 20, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][][] PARAMS = {
            {{27, 0}, {207, 12}, {179, 142}, {0, 117}},
            {{28, 0}, {207, 13}, {180, 137}, {0, 117}}};
    private static final int[][] POS = {{30, 16}, {34, 17}};
    private static final int[] INDEXES = {0, 1, 2, 1, 2, 3, 4, 5, 4, 5, 4, 5, 4, 5, 4, 5};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.resizeFit(images.get(0).image(), 175, 120, ImageEdit.Fit.COVER);
        List<io.github.humbleui.skija.Image> rawFrames = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            rawFrames.add(MemeResources.loadImage("capoo_draw/%d.png".formatted(i)));
        }
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            int index = INDEXES[i];
            var frame = rawFrames.get(index);
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            if (index >= 4 && index < 6) {
                int[][] p = PARAMS[index - 4];
                var persp = ImageEdit.perspective(image, p[0][0], p[0][1], p[1][0], p[1][1], p[2][0], p[2][1], p[3][0], p[3][1]);
                canvas.drawImage(persp, POS[index - 4][0], POS[index - 4][1]);
            }
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
