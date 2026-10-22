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

/** 移植自 memes/upside_down.rs。 */
@Component
public class UpsideDownMeme extends AbstractMeme {

    public UpsideDownMeme() {
        super(new MemeInfo("upside_down",
                new MemeParams(1, 1, 0, 1, List.of("我看你们是反了！"), List.of()),
                List.of("反了"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 24, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 24, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "我看你们是反了！" : texts.get(0);
        int imgW = 500;
        int textH = 80;
        var textSurface = meme.utils.TextRenderer.newSurface(imgW, textH);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(20, 0, imgW - 20, textH),
                text, 30f, 55f, meme.utils.TextRenderer.params());
        var textImage = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.rotate(ImageEdit.resizeWidth(input, imgW), 180f);
            int imgH = img.getHeight();
            var surface = meme.utils.TextRenderer.newSurface(imgW, imgH + textH);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(textImage, 0, 0);
            canvas.drawImage(img, 0, textH);
            return surface.makeImageSnapshot();
        });
    }
}
