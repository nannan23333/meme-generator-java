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

/** 移植自 memes/thump_wildly.rs。 */
@Component
public class ThumpWildlyMeme extends AbstractMeme {

    public ThumpWildlyMeme() {
        super(new MemeInfo("thump_wildly",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("捶爆", "爆捶"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 25, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 25, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 122, 122);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 37; i++) {
            int idx = i >= 31 ? 0 : i;
            var frame = MemeResources.loadImage("thump_wildly/%02d.png".formatted(idx));
            if (idx >= 15 && idx < 31) {
                frames.add(frame);
            } else {
                var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                canvas.clear(0xFFFFFFFF);
                int[] pos = idx == 14 ? new int[]{207, 239} : new int[]{203, 196};
                canvas.drawImage(img, pos[0], pos[1]);
                canvas.drawImage(frame, 0, 0);
                frames.add(surface.makeImageSnapshot());
            }
        }
        return GifSupport.writeGifFromSkija(frames, 0.04f);
    }
}
