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

/** 移植自 memes/genshin_start.rs。 */
@Component
public class GenshinStartMeme extends AbstractMeme {

    public GenshinStartMeme() {
        super(new MemeInfo("genshin_start",
                new MemeParams(1, 1, 0, 1, List.of("原神，启动！"), List.of()),
                List.of("原神启动"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "原神，启动！" : texts.get(0);
        var frame = MemeResources.loadImage("genshin_start/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(100, frame.getHeight() - 150, frame.getWidth() - 100, frame.getHeight()),
                text, 70f, 100f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.1f, 0xFF000000));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            var image = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 600, 330, ImageEdit.Fit.COVER),
                    0, 116, 585, 0, 584, 319, 43, 385);
            canvas.drawImage(image, 412, 121);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
