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

/** 移植自 memes/chino_throw.rs。 */
@Component
public class ChinoThrowMeme extends AbstractMeme {

    public ChinoThrowMeme() {
        super(new MemeInfo("chino_throw",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("智乃扔", "智乃抛"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {133, 299, 240, 186}, {133, 299, 240, 186}, {133, 299, 240, 186}, {133, 299, 240, 186},
            {133, 299, 240, 186}, {93, 260, 250, 220}, {74, 226, 234, 228}, {95, 176, 236, 226},
            {95, 176, 236, 226}, {70, 45, 138, 123}, {104, 51, 128, 112}, {126, 57, 103, 95},
            {49, 23, 190, 146}, {0, 0, 142, 163}, {0, 0, 500, 500}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var frame = MemeResources.loadImage("chino_throw/%02d.png".formatted(i));
            int[] loc = LOCS[Math.min(i, LOCS.length - 1)];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(image, loc[2], loc[3]);
            canvas.drawImage(resized, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.07f);
    }
}
