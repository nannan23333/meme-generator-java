package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/dog_of_vtb.rs。 */
@Component
public class DogOfVtbMeme extends AbstractMeme {

    public DogOfVtbMeme() {
        super(new MemeInfo("dog_of_vtb",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("管人痴"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("dog_of_vtb/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 600, 450, ImageEdit.Fit.COVER),
                    0, 0, 579, 0, 584, 430, 5, 440);
            canvas.drawImage(image, 97, 32);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
