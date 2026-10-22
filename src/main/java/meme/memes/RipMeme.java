package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
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

/** 移植自 memes/rip.rs。 */
@Component
public class RipMeme extends AbstractMeme {

    public RipMeme() {
        super(new MemeInfo("rip",
                new MemeParams(1, 2, 0, 0, List.of(), List.of()),
                List.of("撕"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("rip/0.png");
        var img = images.size() >= 2 ? images.get(1).image() : images.get(0).image();
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var squared = ImageEdit.resizeExact(ImageEdit.square(img), 385, 385);
        canvas.drawImage(ImageEdit.rotate(squared, -24f), -5, 355);
        canvas.drawImage(ImageEdit.rotate(squared, 11f), 649, 310);
        canvas.drawImage(frame, 0, 0);
        if (images.size() >= 2) {
            var circle = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 208, 208);
            canvas.drawImage(circle, 413, 422);
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
