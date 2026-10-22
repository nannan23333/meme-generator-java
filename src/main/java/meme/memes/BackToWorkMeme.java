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

/** back_to_work 继续干活/打工人：移植自 memes/back_to_work.rs。 */
@Component
public class BackToWorkMeme extends AbstractMeme {

    public BackToWorkMeme() {
        super(new MemeInfo("back_to_work",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("继续干活", "打工人"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 10, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("back_to_work/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.rotate(ImageEdit.resizeFit(input, 220, 310, ImageEdit.Fit.COVER), -25f);
            canvas.drawImage(image, 56, 32);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
