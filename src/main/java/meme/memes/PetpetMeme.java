package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeOption;
import meme.core.MemeParams;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * petpet 摸头：GIF，1 输入图，选项 circle（圆形裁剪，默认 true）。
 * 移植自 memes/petpet.rs。
 */
@Component
public class PetpetMeme extends AbstractMeme {

    private static final int[][] LOCS = {
            {14, 20, 98, 98},
            {12, 33, 101, 85},
            {8, 40, 110, 76},
            {10, 33, 102, 84},
            {12, 20, 98, 98},
    };

    private static final java.time.ZoneOffset ZONE = java.time.ZoneOffset.ofHours(8);

    public PetpetMeme() {
        super(new MemeInfo(
                "petpet",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(
                        MemeOption.bool("circle", false))),
                List.of("摸", "摸摸", "摸头", "rua"),
                List.of(),
                Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 8, 1, 0, 0), ZONE),
                OffsetDateTime.of(LocalDateTime.of(2021, 8, 1, 0, 0), ZONE)));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        boolean circle = boolOption(options, "circle", false);
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var hand = MemeResources.loadImage("petpet/%d.png".formatted(outIdx));
            io.github.humbleui.skija.Image input;
            if (images.get(0).isMultiFrame()) {
                input = ImageEdit.fromBufferedImage(
                        images.get(0).gifFrames().get(inIdx).image());
            } else {
                input = images.get(0).image();
            }
            input = ImageEdit.square(input);
            if (circle) {
                input = ImageEdit.circle(input);
            }
            int[] loc = LOCS[outIdx];
            input = ImageEdit.resizeExact(input, loc[2], loc[3]);

            var surface = meme.utils.TextRenderer.newSurface(hand.getWidth(), hand.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(input, loc[0], loc[1]);
            canvas.drawImage(hand, 0, 0);
            return surface.makeImageSnapshot();
        }, 5, 0.06f);
    }
}
