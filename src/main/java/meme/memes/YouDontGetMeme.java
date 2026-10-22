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

/** 移植自 memes/you_dont_get.rs。 */
@Component
public class YouDontGetMeme extends AbstractMeme {

    public YouDontGetMeme() {
        super(new MemeInfo("you_dont_get",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("你不懂啦"), List.of(), Set.of("咖波"),
                OffsetDateTime.of(LocalDateTime.of(2025, 5, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 5, 15, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeFit(images.get(0).image(), 142, 139, ImageEdit.Fit.COVER);
        var frame = MemeResources.loadImage("you_dont_get/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, 217, 181);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
