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

/** 移植自 memes/teach.rs。 */
@Component
public class TeachMeme extends AbstractMeme {

    public TeachMeme() {
        super(new MemeInfo("teach",
                new MemeParams(1, 1, 0, 1, List.of("我老婆") , List.of()),
                List.of("讲课", "敲黑板"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 25, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 25, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "好好学习，天天向上" : texts.get(0);
        var frameTpl = ImageEdit.resizeWidth(MemeResources.loadImage("teach/0.png"), 960);
        var textSurface = meme.utils.TextRenderer.newSurface(frameTpl.getWidth(), frameTpl.getHeight());
        textSurface.getCanvas().drawImage(frameTpl, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(10, frameTpl.getHeight() - 80, frameTpl.getWidth() - 10, frameTpl.getHeight() - 5),
                text, 20f, 50f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.12f, 0xFF000000));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeFit(input, 550, 395, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 313, 60);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
