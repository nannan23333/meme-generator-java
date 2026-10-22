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

/** 移植自 memes/police.rs。 */
@Component
public class PoliceMeme extends AbstractMeme {

    public PoliceMeme() {
        super(new MemeInfo("police",
                new MemeParams(1, 1, 0, 1, List.of("平安名すみれ"), List.of()),
                List.of("出警"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 2, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "平安名すみれ" : texts.get(0);
        var frame = MemeResources.loadImage("police/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeExact(ImageEdit.square(input), 245, 245);
            canvas.drawImage(img, 224, 46);
            canvas.drawImage(frame, 0, 0);
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeXYWH(220, 395, 250, 85),
                    text, 20f, 40f,
                    meme.utils.TextRenderer.params().fontFamilies("NotoSerifSC-Regular.otf"));
            return surface.makeImageSnapshot();
        });
    }
}
