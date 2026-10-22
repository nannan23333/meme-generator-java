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

/** 移植自 memes/learn.rs。 */
@Component
public class LearnMeme extends AbstractMeme {

    public LearnMeme() {
        super(new MemeInfo("learn",
                new MemeParams(1, 1, 0, 1, List.of("偷学群友数理基础"), List.of()),
                List.of("偷学"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 4, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 4, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "偷学群友数理基础" : texts.get(0);
        var frame = MemeResources.loadImage("learn/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(100, 1360, frame.getWidth() - 100, 1730),
                text, 200f, 350f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var img = ImageEdit.resizeFit(input, 1751, 1347, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 1440, 0);
            return surface.makeImageSnapshot();
        });
    }
}
