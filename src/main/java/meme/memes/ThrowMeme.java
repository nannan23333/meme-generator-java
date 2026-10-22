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

/** 移植自 memes/throw.rs。 */
@Component
public class ThrowMeme extends AbstractMeme {

    public ThrowMeme() {
        super(new MemeInfo("throw",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("丢", "扔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        float angle = 1 + (float) (Math.random() * 360);
        var img = ImageEdit.resizeExact(ImageEdit.rotateCrop(ImageEdit.circle(images.get(0).image()), angle), 143, 143);
        var frame = MemeResources.loadImage("throw/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(img, 15, 178);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
