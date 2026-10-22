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

/** 移植自 memes/thermometer_gun.rs。 */
@Component
public class ThermometerGunMeme extends AbstractMeme {

    public ThermometerGunMeme() {
        super(new MemeInfo("thermometer_gun",
                new MemeParams(1, 1, 0, 1, List.of("笨蛋") , List.of()),
                List.of("体温枪"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "39.2℃" : texts.get(0);
        var textSurface = meme.utils.TextRenderer.newSurface(200, 125);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeXYWH(0, 0, 200, 125),
                text, 15f, 60f,
                meme.utils.TextRenderer.params().fontFamilies("FZKaTong-M19S"));
        var textFrame = textSurface.makeImageSnapshot();

        var img = images.get(0).image();
        int imgW = img.getWidth();
        int imgH = img.getHeight();
        int size, posX, posY;
        if (imgW > imgH) {
            size = imgH; posX = imgW - imgH; posY = 0;
        } else {
            size = imgW; posX = 0; posY = imgH - imgW;
        }
        var frameTpl = MemeResources.loadImage("thermometer_gun/0.png");
        var frameSurface = meme.utils.TextRenderer.newSurface(frameTpl.getWidth(), frameTpl.getHeight());
        frameSurface.getCanvas().drawImage(frameTpl, 0, 0);
        frameSurface.getCanvas().drawImage(textFrame, 555, 240);
        var frame = ImageEdit.resizeExact(frameSurface.makeImageSnapshot(), size, size);

        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(input.getWidth(), input.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(input, 0, 0);
            canvas.drawImage(frame, posX, posY);
            return surface.makeImageSnapshot();
        });
    }
}
