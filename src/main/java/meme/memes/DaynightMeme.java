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

/** 移植自 memes/daynight.rs。 */
@Component
public class DaynightMeme extends AbstractMeme {

    public DaynightMeme() {
        super(new MemeInfo("daynight",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("白天黑夜", "白天晚上"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("daynight/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var img1 = ImageEdit.resizeFit(input, 333, 360, ImageEdit.Fit.COVER);
            var img2 = ImageEdit.resizeFit(images.get(1).image(), 333, 360, ImageEdit.Fit.COVER);
            canvas.drawImage(img1, 349, 0);
            canvas.drawImage(img2, 349, 361);
            return surface.makeImageSnapshot();
        });
    }
}
