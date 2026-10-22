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

/** think_what 想什么：移植自 memes/think_what.rs。 */
@Component
public class ThinkWhatMeme extends AbstractMeme {

    public ThinkWhatMeme() {
        super(new MemeInfo("think_what",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("想什么"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 11, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("think_what/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.resizeFit(input, 534, 493, ImageEdit.Fit.COVER);
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, 530, 0);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
