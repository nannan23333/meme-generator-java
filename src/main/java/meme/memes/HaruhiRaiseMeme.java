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

/** 移植自 memes/haruhi_raise.rs。 */
@Component
public class HaruhiRaiseMeme extends AbstractMeme {

    public HaruhiRaiseMeme() {
        super(new MemeInfo("haruhi_raise",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("凉宫春日举"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("haruhi_raise/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 250, 180, ImageEdit.Fit.COVER),
                    0, 51, 204, 0, 211, 147, 17, 217);
            canvas.drawImage(image, 429, 79);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
