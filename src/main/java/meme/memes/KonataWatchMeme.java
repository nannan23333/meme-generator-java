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

/** 移植自 memes/konata_watch.rs。 */
@Component
public class KonataWatchMeme extends AbstractMeme {

    public KonataWatchMeme() {
        super(new MemeInfo("konata_watch",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("泉此方看"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("konata_watch/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 270, 200, ImageEdit.Fit.COVER),
                    0, 1, 275, 0, 273, 202, 2, 216);
            canvas.drawImage(image, 50, 188);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
