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

/** 移植自 memes/karyl_point.rs。 */
@Component
public class KarylPointMeme extends AbstractMeme {

    public KarylPointMeme() {
        super(new MemeInfo("karyl_point",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("凯露指"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 8, 31, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("karyl_point/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.rotate(ImageEdit.resizeExact(ImageEdit.square(input), 200, 200), -7.5f);
            canvas.drawImage(image, 87, 790);
            return surface.makeImageSnapshot();
        });
    }
}
