package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
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

/** dinosaur 恐龙：移植自 memes/dinosaur.rs。 */
@Component
public class DinosaurMeme extends AbstractMeme {

    public DinosaurMeme() {
        super(new MemeInfo("dinosaur",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("恐龙", "小恐龙"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 6, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("dinosaur/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var image = ImageEdit.resizeFit(input, 680, 578, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 294, 369);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
