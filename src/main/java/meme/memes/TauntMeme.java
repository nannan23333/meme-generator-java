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

/** 移植自 memes/taunt.rs。 */
@Component
public class TauntMeme extends AbstractMeme {

    public TauntMeme() {
        super(new MemeInfo("taunt",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("嘲讽"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 7, 19, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 7, 19, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("taunt/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var img = ImageEdit.resizeExact(ImageEdit.square(input), 230, 230);
            canvas.drawImage(img, 245, 245);
            return surface.makeImageSnapshot();
        });
    }
}
