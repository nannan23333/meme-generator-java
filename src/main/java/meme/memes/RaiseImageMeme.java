package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/raise_image.rs。 */
@Component
public class RaiseImageMeme extends AbstractMeme {

    public RaiseImageMeme() {
        super(new MemeInfo("raise_image",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("举"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("raise_image/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var img = ImageEdit.resizeFit(input, 599, 386, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 134, 91);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
