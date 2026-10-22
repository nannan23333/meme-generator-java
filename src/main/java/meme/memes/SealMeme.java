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

/** 移植自 memes/seal.rs。 */
@Component
public class SealMeme extends AbstractMeme {

    public SealMeme() {
        super(new MemeInfo("seal",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("源石封印"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int width = ImageEdit.square(images.get(0).image()).getWidth();
        var mask = ImageEdit.resizeExact(MemeResources.loadImage("seal/0.png"), width, width);
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.square(input);
            var surface = meme.utils.TextRenderer.newSurface(img.getWidth(), img.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(mask, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
