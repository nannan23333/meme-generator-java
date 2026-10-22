package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/speechless.rs。 */
@Component
public class SpeechlessMeme extends AbstractMeme {

    public SpeechlessMeme() {
        super(new MemeInfo("speechless",
                new MemeParams(1, 1, 0, 1, List.of(), List.of()),
                List.of("无语"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = "无语，和你说不下去";
        if (!texts.isEmpty()) {
            text += "\n" + texts.get(0);
        }
        var sweat = ImageEdit.resizeExact(MemeResources.loadImage("speechless/sweat.png"), 80, 80);
        var textImg = Text2Image.fromText(text, 45f, null, null, null, null);
        int textW = 480;
        textImg.layout(textW);
        int textH = Math.round(textImg.height());
        return InputImage.makePngOrGif(images.get(0), input -> {
            int frameW = 500;
            var img = ImageEdit.resizeWidth(input, frameW);
            int frameH = img.getHeight() + textH + 10;
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, 0, 0);
            textImg.drawOnCanvas(canvas, (frameW - textW) / 2f, img.getHeight());
            canvas.drawImage(sweat, 300, 120);
            return surface.makeImageSnapshot();
        });
    }
}
