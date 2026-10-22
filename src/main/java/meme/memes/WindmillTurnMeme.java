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

/** 移植自 memes/windmill_turn.rs。 */
@Component
public class WindmillTurnMeme extends AbstractMeme {

    public WindmillTurnMeme() {
        super(new MemeInfo("windmill_turn",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("风车转"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 12, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var surface = meme.utils.TextRenderer.newSurface(600, 600);
            var canvas = surface.getCanvas();
            var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 300, 300);
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(ImageEdit.rotate(img, 90f), 0, 300);
            canvas.drawImage(ImageEdit.rotate(img, 180f), 300, 300);
            canvas.drawImage(ImageEdit.rotate(img, 270f), 300, 0);
            var frame = surface.makeImageSnapshot();
            return ImageEdit.crop(ImageEdit.rotateCrop(frame, outIdx * 18f), 50, 50, 550, 550);
        }, 5, 0.05f);
    }
}
