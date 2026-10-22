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

/** 移植自 memes/subject3.rs。 */
@Component
public class Subject3Meme extends AbstractMeme {

    public Subject3Meme() {
        super(new MemeInfo("subject3",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("科目三"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 4, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 4, 17, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {60, 71},
            {61, 73},
            {62, 71},
            {66, 70},
            {75, 69},
            {87, 74},
            {87, 74},
            {85, 76},
            {79, 73},
            {76, 71},
            {68, 69},
            {66, 73},
            {66, 74},
            {66, 74},
            {66, 71},
            {76, 65},
            {80, 65},
            {91, 73},
            {91, 77},
            {91, 75},
            {86, 71},
            {83, 69},
            {78, 68},
            {73, 67},
            {68, 74},
            {68, 77},
            {71, 73},
            {81, 68},
            {88, 69},
            {96, 73},
            {98, 78},
            {97, 79},
            {93, 76},
            {85, 71},
            {80, 66},
            {71, 69},
            {69, 74},
            {68, 77},
            {68, 77},
            {80, 70},
            {91, 68},
            {95, 71},
            {98, 78},
            {97, 79},
            {95, 78},
            {86, 69},
            {77, 64},
            {71, 69},
            {71, 73},
            {69, 73},
            {73, 67},
            {78, 65},
            {88, 65},
            {91, 72},
            {94, 77},
            {91, 74},
            {89, 70},
            {83, 63},
            {75, 60},
            {69, 67},
            {67, 74},
            {68, 73},
            {76, 64},
            {77, 60},
            {84, 62},
            {92, 68},
            {92, 73},
            {90, 69},
            {86, 66},
            {80, 61},
            {69, 63},
            {65, 67},
            {60, 76},
            {62, 73},
            {66, 68},
            {75, 62},
            {77, 62},
            {85, 69},
            {86, 73},
            {85, 75},
            {78, 70},
            {74, 67},
            {67, 67},
            {65, 72},
            {65, 79}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeExact(ImageEdit.circle(input), 120, 120);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 85; i++) {
            var frame = MemeResources.loadImage("subject3/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(img, LOCS[i][0], LOCS[i][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
