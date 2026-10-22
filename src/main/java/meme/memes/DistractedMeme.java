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

/** distracted 注意力涣散：移植自 memes/distracted.rs。 */
@Component
public class DistractedMeme extends AbstractMeme {

    public DistractedMeme() {
        super(new MemeInfo("distracted",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("注意力涣散"), List.of(), Set.of("明日方舟"),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("distracted/1.png");
        var label = MemeResources.loadImage("distracted/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var image = ImageEdit.resizeExact(ImageEdit.square(input), 500, 500);
            var surface = meme.utils.TextRenderer.newSurface(image.getWidth(), image.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(image, 0, 0);
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(label, 140, 320);
            return surface.makeImageSnapshot();
        });
    }
}
