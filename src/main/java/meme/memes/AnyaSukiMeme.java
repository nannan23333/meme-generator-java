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

/** anya_suki 阿尼亚喜欢：移植自 memes/anya_suki.rs。 */
@Component
public class AnyaSukiMeme extends AbstractMeme {

    public AnyaSukiMeme() {
        super(new MemeInfo("anya_suki",
                new MemeParams(1, 1, 0, 0 , List.of("阿尼亚喜欢这个"), List.of()),
                List.of("阿尼亚喜欢"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 10, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "阿尼亚喜欢这个" : texts.get(0);
        var frame = MemeResources.loadImage("anya_suki/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(5, frame.getHeight() - 60, frame.getWidth() - 5, frame.getHeight() - 10),
                text, 20f, 40f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.125f, 0xFF000000));
        var composed = textSurface.makeImageSnapshot();

        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            var image = ImageEdit.resizeFit(input, 305, 235, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 106, 72);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
