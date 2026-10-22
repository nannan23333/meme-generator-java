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

/** 移植自 memes/time_to_go.rs。 */
@Component
public class TimeToGoMeme extends AbstractMeme {

    public TimeToGoMeme() {
        super(new MemeInfo("time_to_go",
                new MemeParams(1, 1, 0, 1, List.of("说完了吗？该走了"), List.of()),
                List.of("该走了"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "说完了吗？该走了" : texts.get(0);
        var frame = MemeResources.loadImage("time_to_go/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeExact(ImageEdit.square(input), 105, 105);
            canvas.drawImage(img, 230, 82);
            canvas.drawImage(frame, 0, 0);
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeLTRB(20, 232, 330, 312),
                    text, 20f, 40f, meme.utils.TextRenderer.params());
            return surface.makeImageSnapshot();
        });
    }
}
