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

/** dont_go_near 不要靠近：移植自 memes/dont_go_near.rs。 */
@Component
public class DontGoNearMeme extends AbstractMeme {

    public DontGoNearMeme() {
        super(new MemeInfo("dont_go_near",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("不要靠近"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 1, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 20, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("dont_go_near/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.resizeExact(ImageEdit.square(input), 170, 170);
            canvas.drawImage(image, 23, 231);
            return surface.makeImageSnapshot();
        });
    }
}
