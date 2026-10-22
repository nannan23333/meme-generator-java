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

/** 移植自 memes/arona_throw.rs。 */
@Component
public class AronaThrowMeme extends AbstractMeme {

    public AronaThrowMeme() {
        super(new MemeInfo("arona_throw",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("阿罗娜扔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 10, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 10, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {270, 295},
            {154, 291},
            {154, 291},
            {89, 211},
            {41, 195},
            {28, 192},
            {16, 200},
            {-10, 206},
            {-40, 210},
            {-80, 214},
            {324, 245},
            {324, 256},
            {331, 251},
            {331, 251},
            {318, 260},
            {318, 260}
    };
    private static final int[][] LOCS2 = {
            {324, 15},
            {324, 106},
            {324, 161},
            {324, 192}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var pyroxenes = ImageEdit.resizeFit(ImageEdit.circle(input), 120, 120, ImageEdit.Fit.COVER);
            var arona = MemeResources.loadImage("arona_throw/%02d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(arona.getWidth(), arona.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(arona, 0, 0);
            canvas.drawImage(pyroxenes, LOCS[outIdx][0], LOCS[outIdx][1]);
            if (outIdx >= 6 && outIdx <= 9) {
                canvas.drawImage(pyroxenes, LOCS2[outIdx - 6][0], LOCS2[outIdx - 6][1]);
            }
            return surface.makeImageSnapshot();
        }, 16, 0.04f);
    }
}
