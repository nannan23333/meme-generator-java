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

/** 移植自 memes/beat_head.rs。 */
@Component
public class BeatHeadMeme extends AbstractMeme {

    public BeatHeadMeme() {
        super(new MemeInfo("beat_head",
                new MemeParams(1, 1, 0, 1, List.of("怎么说话的你"), List.of()),
                List.of("拍头"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {{160, 121, 76, 76}, {172, 124, 69, 69}, {208, 166, 52, 52}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "怎么说话的你" : texts.get(0);
        var image = ImageEdit.circle(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int[] loc = LOCS[i];
            var head = ImageEdit.resizeExact(image, loc[2], loc[3]);
            var frame = MemeResources.loadImage("beat_head/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(head, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeLTRB(175, 28, 316, 82),
                    text, 10f, 50f, meme.utils.TextRenderer.params());
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
