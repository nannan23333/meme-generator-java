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

/** 移植自 memes/walnut_pad.rs。 */
@Component
public class WalnutPadMeme extends AbstractMeme {

    public WalnutPadMeme() {
        super(new MemeInfo("walnut_pad",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("胡桃平板"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 8, 11, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 8, 11, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("walnut_pad/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.resizeFit(input, 540, 360, ImageEdit.Fit.COVER);
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, 368, 65);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
