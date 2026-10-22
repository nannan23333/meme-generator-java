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

/** 移植自 memes/grab_both_hands.rs。 */
@Component
public class GrabBothHandsMeme extends AbstractMeme {

    public GrabBothHandsMeme() {
        super(new MemeInfo("grab_both_hands",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("双手抓"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 1, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("grab_both_hands/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var img = ImageEdit.resizeFit(input, frame.getWidth(), frame.getHeight(), ImageEdit.Fit.COVER);
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
