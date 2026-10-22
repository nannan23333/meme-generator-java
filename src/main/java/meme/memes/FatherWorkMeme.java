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

/** 移植自 memes/father_work.rs。 */
@Component
public class FatherWorkMeme extends AbstractMeme {

    public FatherWorkMeme() {
        super(new MemeInfo("father_work",
                new MemeParams(1, 1, 1, 1, List.of() , List.of()),
                List.of("我爸爸"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 17, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("father_work/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(195, frame.getHeight() - 110, frame.getWidth() - 10, frame.getHeight() - 20),
                text, 10f, 50f, meme.utils.TextRenderer.params());
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var image = ImageEdit.resizeFit(input, 230, 120, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(image, 252, 142);
            return surface.makeImageSnapshot();
        });
    }
}
