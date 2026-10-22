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

/** 移植自 memes/safe_sense.rs。 */
@Component
public class SafeSenseMeme extends AbstractMeme {

    public SafeSenseMeme() {
        super(new MemeInfo("safe_sense",
                new MemeParams(1, 1, 0, 0 , List.of("你给我的安全感\n远不及他的万分之一"), List.of()),
                List.of("安全感"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 24, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "你给我的安全感\n远不及他的万分之一" : texts.get(0);
        var frame = MemeResources.loadImage("safe_sense/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(30, 0, 400, 130),
                text, 25f, 50f, meme.utils.TextRenderer.params());
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var img = ImageEdit.resizeFit(input, 215, 343, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 215, 135);
            return surface.makeImageSnapshot();
        });
    }
}
