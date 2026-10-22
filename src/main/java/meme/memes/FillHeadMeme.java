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

/** 移植自 memes/fill_head.rs。 */
@Component
public class FillHeadMeme extends AbstractMeme {

    public FillHeadMeme() {
        super(new MemeInfo("fill_head",
                new MemeParams(1, 1, 0, 0 , List.of() , List.of()),
                List.of("满脑子"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        String text = "满脑子都是" + (name == null || name.isEmpty() ? "?" : name);
        var frame = MemeResources.loadImage("fill_head/0.jpg");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(20, 458, frame.getWidth() - 20, 550),
                text, 30f, 65f, meme.utils.TextRenderer.params());
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var image = ImageEdit.resizeFit(input, 210, 170, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 150, 2);
            return surface.makeImageSnapshot();
        });
    }
}
