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

/** 移植自 memes/maimai_awaken.rs。 */
@Component
public class MaimaiAwakenMeme extends AbstractMeme {

    public MaimaiAwakenMeme() {
        super(new MemeInfo("maimai_awaken",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("旅行伙伴觉醒"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 31, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 31, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("maimai_awaken/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.rotate(ImageEdit.resizeExact(ImageEdit.square(input), 250, 250), 25f);
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, 134, 134);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
