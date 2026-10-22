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

/** 移植自 memes/jerry_stare.rs。 */
@Component
public class JerryStareMeme extends AbstractMeme {

    public JerryStareMeme() {
        super(new MemeInfo("jerry_stare",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("杰瑞盯"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("jerry_stare/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var image = ImageEdit.resizeExact(ImageEdit.circle(input), 150, 150);
            canvas.drawImage(image, 184, 268);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
