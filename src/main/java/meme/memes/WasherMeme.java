package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
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

/** 移植自 memes/washer.rs。 */
@Component
public class WasherMeme extends AbstractMeme {

    public WasherMeme() {
        super(new MemeInfo("washer",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("洗衣机"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("washer/0.png");
        var img = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var rotated = ImageEdit.resizeExact(ImageEdit.rotateCrop(img, i * 30f), 74, 74);
            canvas.drawImage(rotated, 63, 56);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
