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

/** 移植自 memes/kirby_hammer.rs。 */
@Component
public class KirbyHammerMeme extends AbstractMeme {

    public KirbyHammerMeme() {
        super(new MemeInfo("kirby_hammer",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(MemeOption.bool("circle", false))),
                List.of("卡比锤", "卡比重锤"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 27, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] POSITIONS = {
            {318, 163}, {319, 173}, {320, 183}, {317, 193}, {312, 199}, {297, 212}, {289, 218},
            {280, 224}, {278, 223}, {278, 220}, {280, 215}, {280, 213}, {280, 210}, {280, 206},
            {280, 201}, {280, 192}, {280, 188}, {280, 184}, {280, 179}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        boolean circle = boolOption(options, "circle", false);
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("kirby_hammer/%02d.png".formatted(outIdx));
            if (outIdx > 39) {
                return frame;
            }
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var image = circle ? ImageEdit.circle(input) : input;
            image = ImageEdit.resizeHeight(image, 80);
            if (image.getWidth() < 80) {
                image = ImageEdit.resizeFit(image, 80, 80, ImageEdit.Fit.COVER);
            }
            int[] pos = outIdx <= 18 ? POSITIONS[outIdx] : POSITIONS[18];
            int px = pos[0] + 40 - image.getWidth() / 2;
            int py = pos[1];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(image, px, py);
            return surface.makeImageSnapshot();
        }, 62, 0.05f);
    }
}
