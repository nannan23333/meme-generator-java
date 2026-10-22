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

/** 移植自 memes/tom_tease.rs。 */
@Component
public class TomTeaseMeme extends AbstractMeme {

    public TomTeaseMeme() {
        super(new MemeInfo("tom_tease",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("汤姆嘲笑"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 16, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("tom_tease/%02d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.perspective(
                    ImageEdit.resizeFit(images.get(0).image(), 400, 350, ImageEdit.Fit.COVER),
                    0, 100, 290, 0, 290, 370, 0, 335);
            canvas.drawImage(img, 258, -12);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 11, 0.2f);
    }
}
