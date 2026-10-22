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

/** 移植自 memes/together.rs。 */
@Component
public class TogetherMeme extends AbstractMeme {

    public TogetherMeme() {
        super(new MemeInfo("together",
                new MemeParams(1, 1, 0, 1, List.of(), List.of()),
                List.of("一起"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "一起玩" + images.get(0).name() + "吧！" : texts.get(0);
        var frame = MemeResources.loadImage("together/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var tcanvas = textSurface.getCanvas();
            tcanvas.drawImage(frame, 0, 0);
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    tcanvas,
                    io.github.humbleui.types.Rect.makeLTRB(10, 140, 190, 190),
                    text, 10f, 50f,
                    meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
            var composed = textSurface.makeImageSnapshot();

            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var img = ImageEdit.resizeExact(ImageEdit.square(input), 63, 63);
            canvas.drawImage(img, 132, 36);
            return surface.makeImageSnapshot();
        });
    }
}
