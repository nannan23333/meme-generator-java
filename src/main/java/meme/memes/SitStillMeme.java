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

/** 移植自 memes/sit_still.rs。 */
@Component
public class SitStillMeme extends AbstractMeme {

    public SitStillMeme() {
        super(new MemeInfo("sit_still",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("坐得住"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 11, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("sit_still/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 150, 150);
        canvas.drawImage(img, 268, 344);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
