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

/** 移植自 memes/throw_gif.rs。 */
@Component
public class ThrowGifMeme extends AbstractMeme {

    public ThrowGifMeme() {
        super(new MemeInfo("throw_gif",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("抛", "掷"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 11, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][][] PARAMS = {
            {{32, 32, 108, 36}}, {{32, 32, 122, 36}}, {},
            {{123, 123, 19, 129}},
            {{185, 185, -50, 200}, {33, 33, 289, 70}},
            {{32, 32, 280, 73}}, {{35, 35, 259, 31}},
            {{175, 175, -50, 220}}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.circle(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            var frame = MemeResources.loadImage("throw_gif/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            for (int[] p : PARAMS[i]) {
                var resized = ImageEdit.resizeExact(img, p[0], p[1]);
                canvas.drawImage(resized, p[2], p[3]);
            }
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
