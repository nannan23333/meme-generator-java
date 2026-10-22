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

/** 移植自 memes/play_together.rs。 */
@Component
public class PlayTogetherMeme extends AbstractMeme {

    public PlayTogetherMeme() {
        super(new MemeInfo("play_together",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("一起玩"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108}, {194, 204, 121, 108},
            {192, 203, 125, 111}, {182, 200, 141, 126}, {161, 188, 178, 159}, {129, 171, 235, 209},
            {98, 155, 290, 258}, {98, 155, 290, 258}, {58, 133, 361, 321}, {45, 126, 384, 342},
            {45, 126, 384, 342}, {35, 121, 402, 358}, {27, 117, 415, 370}, {17, 111, 433, 386},
            {14, 110, 439, 391}, {14, 110, 439, 391}, {11, 108, 444, 395}, {10, 108, 446, 397},
            {7, 106, 451, 402}, {7, 106, 451, 402}, {7, 106, 451, 402}, {7, 106, 451, 402},
            {7, 106, 451, 402}, {7, 106, 451, 402}, {7, 106, 451, 402}, {7, 106, 451, 402}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            int[] p = PARAMS[outIdx];
            var screen = ImageEdit.resizeFit(images.get(0).image(), p[2], p[3], ImageEdit.Fit.COVER);
            var frame = MemeResources.loadImage("play_together/%02d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(screen, p[0], p[1]);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 60, 0.06f);
    }
}
