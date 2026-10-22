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

/** 移植自 memes/smash.rs。 */
@Component
public class SmashMeme extends AbstractMeme {

    public SmashMeme() {
        super(new MemeInfo("smash",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("砸"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("smash/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var screen = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 800, 500, ImageEdit.Fit.COVER),
                    1, 237, 826, 1, 832, 508, 160, 732);
            canvas.drawImage(screen, -136, -81);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
