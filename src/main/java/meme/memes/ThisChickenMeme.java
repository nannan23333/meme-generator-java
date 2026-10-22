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

/** 移植自 memes/this_chicken.rs。 */
@Component
public class ThisChickenMeme extends AbstractMeme {

    public ThisChickenMeme() {
        super(new MemeInfo("this_chicken",
                new MemeParams(1, 1, 0, 1, List.of("这是十二生肖中的鸡"), List.of()),
                List.of("这是鸡", "🐔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 3, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "这是十二生肖中的鸡" : texts.get(0);
        var frame = MemeResources.loadImage("this_chicken/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(0, 900, 1440, 1080),
                text, 40f, 80f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.125f, 0xFF000000));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 640, 640, ImageEdit.Fit.COVER),
                    507, 0, 940, 351, 383, 625, 0, 256);
            canvas.drawImage(img, 201, 201);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
