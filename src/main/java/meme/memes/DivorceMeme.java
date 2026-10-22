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

/** divorce 离婚协议：移植自 memes/divorce.rs。 */
@Component
public class DivorceMeme extends AbstractMeme {

    public DivorceMeme() {
        super(new MemeInfo("divorce",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("离婚协议", "离婚申请"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 7, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("divorce/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, frame.getWidth(), frame.getHeight(), ImageEdit.Fit.COVER);
            canvas.drawImage(image, 0, 0);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
