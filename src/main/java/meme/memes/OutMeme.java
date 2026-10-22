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

/** 移植自 memes/out.rs。 */
@Component
public class OutMeme extends AbstractMeme {

    public OutMeme() {
        super(new MemeInfo("out",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("out"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frameTpl = MemeResources.loadImage("out/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var frame = ImageEdit.resizeWidth(frameTpl, input.getWidth() / 2);
            var surface = meme.utils.TextRenderer.newSurface(input.getWidth(), input.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            int posX = Math.min(input.getWidth() - frame.getWidth() - 10, input.getWidth() * 4 / 10);
            int posY = Math.min(input.getHeight() - frame.getHeight() - 10, input.getHeight() * 7 / 10);
            canvas.drawImage(input, 0, 0);
            canvas.drawImage(frame, posX, posY);
            return surface.makeImageSnapshot();
        });
    }
}
