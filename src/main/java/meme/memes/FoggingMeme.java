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

/** 移植自 memes/fogging.rs。 */
@Component
public class FoggingMeme extends AbstractMeme {

    public FoggingMeme() {
        super(new MemeInfo("fogging",
                new MemeParams(1, 1, 0, 1, List.of("兄弟，回南了"), List.of()),
                List.of("回南天", "水雾"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 3, 16, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String DEFAULT_TEXT = "兄弟，回南了";

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? DEFAULT_TEXT : texts.get(0);
        var img0 = images.get(0).image();
        int imgW = Math.min(img0.getWidth(), 500);
        int imgH = img0.getHeight() * imgW / img0.getWidth();
        var mask0 = ImageEdit.resizeFit(MemeResources.loadImage("fogging/0.png"), imgW, imgH, ImageEdit.Fit.COVER);
        var maskSurface = meme.utils.TextRenderer.newSurface(imgW, imgH);
        maskSurface.getCanvas().drawImage(mask0, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                maskSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(10, 10, imgW - 10, 80),
                text, 20f, 40f, meme.utils.TextRenderer.params());
        var mask = maskSurface.makeImageSnapshot();

        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.gaussianBlur(ImageEdit.resizeExact(input, imgW, imgH), 10f, 10f);
            var surface = meme.utils.TextRenderer.newSurface(imgW, imgH);
            var canvas = surface.getCanvas();
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(mask, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
