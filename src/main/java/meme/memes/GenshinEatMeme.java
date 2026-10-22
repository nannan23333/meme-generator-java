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

/** 移植自 memes/genshin_eat.rs。 */
@Component
public class GenshinEatMeme extends AbstractMeme {

    public GenshinEatMeme() {
        super(new MemeInfo("genshin_eat",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("character", null, List.of("hutao", "keqing", "klee", "nilou", "yae_miko", "zhongli"), null, meme.core.ParserFlags.none()))),
                List.of("原神吃"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String[] CHARACTERS = {"hutao", "keqing", "klee", "nilou", "yae_miko", "zhongli"};
    private static final int[][] LOCS = {{106, 245}, {115, 224}, {116, 205}, {115, 198}, {120, 217}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String character = stringOption(options, "character", null);
        if (character == null) {
            character = CHARACTERS[(int) (Math.random() * CHARACTERS.length)];
        }
        var image = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 44, 44);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            var frame = MemeResources.loadImage("genshin_eat/%s/%02d.png".formatted(character, i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            if (i >= 4 && i < 9) {
                var img = i == 8 ? ImageEdit.resizeExact(image, 44, 33) : image;
                canvas.drawImage(img, LOCS[i - 4][0], LOCS[i - 4][1]);
            }
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
