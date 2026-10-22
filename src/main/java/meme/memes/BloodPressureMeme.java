package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * blood_pressure 高血压：静态模板，1 输入图。
 * 移植自 memes/blood_pressure.rs。
 */
@Component
public class BloodPressureMeme extends AbstractMeme {

    private static final java.time.ZoneOffset ZONE = java.time.ZoneOffset.ofHours(8);

    public BloodPressureMeme() {
        super(new MemeInfo(
                "blood_pressure",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("高血压"),
                List.of(),
                Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 8, 22, 0, 0), ZONE),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZONE)));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("blood_pressure/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, 414, 450, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 16, 17);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
