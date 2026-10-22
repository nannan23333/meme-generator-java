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

/** 移植自 memes/capoo_strike.rs。 */
@Component
public class CapooStrikeMeme extends AbstractMeme {

    public CapooStrikeMeme() {
        super(new MemeInfo("capoo_strike",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("咖波撞", "咖波头槌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZoneOffset.ofHours(8))));
    }

    // 每项：{x0,y0,x1,y1,x2,y2,x3,y3,px,py}
    private static final int[][] PARAMS = {
            {0, 4, 153, 0, 138, 105, 0, 157, 28, 47},
            {1, 13, 151, 0, 130, 104, 0, 156, 28, 48},
            {9, 10, 156, 0, 152, 108, 0, 155, 18, 51},
            {0, 21, 150, 0, 146, 115, 7, 145, 17, 53},
            {0, 19, 156, 0, 199, 109, 31, 145, 2, 62},
            {0, 28, 156, 0, 171, 115, 12, 154, 16, 58},
            {0, 25, 157, 0, 169, 113, 13, 147, 18, 63}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("capoo_strike/%d.png".formatted(outIdx));
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var image = ImageEdit.resizeFit(input, 200, 160, ImageEdit.Fit.COVER);
            int[] p = PARAMS[outIdx];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var persp = ImageEdit.perspective(image, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
            canvas.drawImage(persp, p[8], p[9]);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 7, 0.05f);
    }
}
