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

/** 移植自 memes/gorilla_throw.rs。 */
@Component
public class GorillaThrowMeme extends AbstractMeme {

    public GorillaThrowMeme() {
        super(new MemeInfo("gorilla_throw",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("猩猩扔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {74, 125, 24, 8, 135}, {84, 119, 24, 8, 130}, {111, 103, 22, 15, 100},
            {122, 95, 22, 15, 90}, {136, 89, 25, 17, 87}, {142, 60, 49, 22, 40},
            {134, 49, 66, 30, 30}, {134, 49, 66, 30, 30}, {116, 35, 92, 38, 25},
            {78, 26, 167, 73, 5}, {-30, 0, 300, 180, -5}, {-120, -85, 400, 240, -16},
            {-160, -125, 500, 300, -20}, {-180, -180, 600, 360, -23}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("gorilla_throw/%02d.png".formatted(outIdx));
            if (outIdx < 28) {
                return frame;
            }
            int[] p = PARAMS[outIdx - 28];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.rotate(ImageEdit.resizeFit(images.get(0).image(), p[2], p[3], ImageEdit.Fit.COVER), p[4]);
            canvas.drawImage(image, p[0], p[1]);
            return surface.makeImageSnapshot();
        }, 42, 0.04f);
    }
}
