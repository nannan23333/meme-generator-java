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
import java.util.Set;

/** addiction 上瘾：静态模板，图贴模板左上。移植自 memes/addiction.rs。 */
@Component
public class AddictionMeme extends AbstractMeme {

    public AddictionMeme() {
        super(new MemeInfo("addiction",
                new MemeParams(1, 1, 0, 1, List.of(), List.of()),
                List.of("上瘾", "毒瘾发作"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 8, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts,
                                 java.util.Map<String, Object> options) {
        var frame = MemeResources.loadImage("addiction/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            // 对齐 Rust：frame 作背景先画，头像再压上去（顺序反了头像会被白色模板盖住）
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.resizeFit(input, 91, 91, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
