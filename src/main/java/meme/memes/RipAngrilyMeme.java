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

/** 移植自 memes/rip_angrily.rs。 */
@Component
public class RipAngrilyMeme extends AbstractMeme {

    public RipAngrilyMeme() {
        super(new MemeInfo("rip_angrily",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("怒撕"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 11, 3, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("rip_angrily/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeExact(ImageEdit.square(input), 105, 105);
            canvas.drawImage(ImageEdit.rotate(img, 24f), 18, 170);
            canvas.drawImage(ImageEdit.rotate(img, -24f), 163, 65);
            // frame 是前景（撕纸边缘遮罩），必须最后画，否则头像不会"被撕开"
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
