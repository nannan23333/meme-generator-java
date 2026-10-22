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

/** 移植自 memes/klee_eat.rs。 */
@Component
public class KleeEatMeme extends AbstractMeme {

    public KleeEatMeme() {
        super(new MemeInfo("klee_eat",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("可莉吃"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 6, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {0, 174}, {0, 174}, {0, 174}, {0, 174}, {0, 174}, {12, 160}, {19, 152}, {23, 148},
            {26, 145}, {32, 140}, {37, 136}, {42, 131}, {49, 127}, {70, 126}, {88, 128},
            {-30, 210}, {-19, 207}, {-14, 200}, {-10, 188}, {-7, 179}, {-3, 170}, {-3, 175},
            {-1, 174}, {0, 174}, {0, 174}, {0, 174}, {0, 174}, {0, 174}, {0, 174}, {0, 174},
            {0, 174}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 83, 83);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            var frame = MemeResources.loadImage("klee_eat/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(image, LOCS[i][0], LOCS[i][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
