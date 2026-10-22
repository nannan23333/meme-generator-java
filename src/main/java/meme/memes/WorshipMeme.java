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

/** 移植自 memes/worship.rs。 */
@Component
public class WorshipMeme extends AbstractMeme {

    public WorshipMeme() {
        super(new MemeInfo("worship",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("膜", "膜拜"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 150, 150);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var frame = MemeResources.loadImage("worship/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var persp = ImageEdit.perspective(img, 0, -30, 135, 17, 135, 145, 0, 140);
            canvas.drawImage(persp, 0, 0);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.04f);
    }
}
