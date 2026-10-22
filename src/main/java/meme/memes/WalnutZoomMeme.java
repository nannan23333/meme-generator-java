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

/** 移植自 memes/walnut_zoom.rs。 */
@Component
public class WalnutZoomMeme extends AbstractMeme {

    public WalnutZoomMeme() {
        super(new MemeInfo("walnut_zoom",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("胡桃放大"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {-222, 30, 695, 430}, {-212, 30, 695, 430}, {0, 30, 695, 430}, {41, 26, 695, 430},
            {-100, -67, 922, 570}, {-172, -113, 1059, 655}, {-273, -192, 1217, 753}};
    private static final int[] SEQ = {0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 6, 6, 6, 6};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("walnut_zoom/%02d.png".formatted(outIdx));
            int[] p = PARAMS[SEQ[outIdx]];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.rotate(ImageEdit.resizeFit(images.get(0).image(), p[2], p[3], ImageEdit.Fit.COVER), -4.2f);
            canvas.drawImage(img, p[0], p[1]);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 24, 0.2f);
    }
}
