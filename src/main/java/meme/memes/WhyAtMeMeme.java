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

/** 移植自 memes/why_at_me.rs。 */
@Component
public class WhyAtMeMeme extends AbstractMeme {

    public WhyAtMeMeme() {
        super(new MemeInfo("why_at_me",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("为什么@我"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 5, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("why_at_me/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.rotateCrop(ImageEdit.resizeExact(ImageEdit.square(input), 265, 265), -19f);
            canvas.drawImage(img, 42, 13);
            // 对齐 Rust：frame 最后画（前景遮罩）
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
