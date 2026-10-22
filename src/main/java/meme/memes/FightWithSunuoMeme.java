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

/** 移植自 memes/fight_with_sunuo.rs。 */
@Component
public class FightWithSunuoMeme extends AbstractMeme {

    public FightWithSunuoMeme() {
        super(new MemeInfo("fight_with_sunuo",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("我打宿傩", "我打宿傩吗"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 2, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("fight_with_sunuo/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var image = ImageEdit.grayscale(ImageEdit.resizeFit(input, 565, 1630, ImageEdit.Fit.COVER));
            canvas.drawImage(image, 0, 245);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
