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

/** 移植自 memes/paint.rs。 */
@Component
public class PaintMeme extends AbstractMeme {

    public PaintMeme() {
        super(new MemeInfo("paint",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("这像画吗"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 11, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("paint/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.rotate(ImageEdit.resizeFit(input, 117, 135, ImageEdit.Fit.COVER), -4f);
            canvas.drawImage(img, 95, 107);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
