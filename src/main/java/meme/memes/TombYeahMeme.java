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

/** 移植自 memes/tomb_yeah.rs。 */
@Component
public class TombYeahMeme extends AbstractMeme {

    public TombYeahMeme() {
        super(new MemeInfo("tomb_yeah",
                new MemeParams(1, 2, 0, 0, List.of(), List.of()),
                List.of("上坟", "坟前比耶"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 11, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 11, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("tomb_yeah/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 145, 145);
        canvas.drawImage(img, 138, 265);
        if (images.size() > 1) {
            var img2 = ImageEdit.resizeExact(
                    ImageEdit.rotateCrop(ImageEdit.circle(images.get(1).image()), -30f), 145, 145);
            canvas.drawImage(img2, 371, 312);
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
