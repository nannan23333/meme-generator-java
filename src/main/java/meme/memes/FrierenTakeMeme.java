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

/** 移植自 memes/frieren_take.rs。 */
@Component
public class FrierenTakeMeme extends AbstractMeme {

    public FrierenTakeMeme() {
        super(new MemeInfo("frieren_take",
                new MemeParams(1, 1, 0, 1, List.of("所谓的男人啊，只要送他们这种东西就会很开心"), List.of()),
                List.of("芙莉莲拿"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 20, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "所谓的男人啊，只要送他们这种东西就会很开心" : texts.get(0);
        var frame = MemeResources.loadImage("frieren_take/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(100, frame.getHeight() - 120, frame.getWidth() - 100, frame.getHeight()),
                text, 20f, 30f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.13f, 0xFF000000));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, 102, 108, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 130, 197);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
