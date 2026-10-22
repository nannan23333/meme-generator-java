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

/** 移植自 memes/stew.rs。 */
@Component
public class StewMeme extends AbstractMeme {

    public StewMeme() {
        super(new MemeInfo("stew",
                new MemeParams(1, 1, 0, 1, List.of(), List.of()),
                List.of("炖"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "生活不易,炖" + images.get(0).name() + "出气" : texts.get(0);
        var frame = MemeResources.loadImage("stew/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(2, frame.getHeight() - 30, frame.getWidth() - 2, frame.getHeight()),
                text, 6f, 30f, meme.utils.TextRenderer.params());
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, 181, 154, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 9, -2);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
