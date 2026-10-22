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

/** 移植自 memes/plana_eat.rs。 */
@Component
public class PlanaEatMeme extends AbstractMeme {

    public PlanaEatMeme() {
        super(new MemeInfo("plana_eat",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("普拉娜吃", "普拉娜舔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 6, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 6, 22, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("plana_eat/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeFit(input, 488, 488, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 212, 535);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
