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

/** 移植自 memes/shiroko_pero.rs。 */
@Component
public class ShirokoPeroMeme extends AbstractMeme {

    public ShirokoPeroMeme() {
        super(new MemeInfo("shiroko_pero",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("白子舔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 28, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 28, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var mask = MemeResources.loadImage("shiroko_pero/mask.png");
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("shiroko_pero/%d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var img = ImageEdit.applyMask(
                    ImageEdit.withBackground(ImageEdit.resizeFit(images.get(0).image(), 245, 245, ImageEdit.Fit.COVER)),
                    mask);
            canvas.drawImage(img, 105, 178);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 4, 0.06f);
    }
}
