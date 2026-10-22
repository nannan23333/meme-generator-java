package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
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

/** always_like 我永远喜欢：移植自 memes/always_like.rs。 */
@Component
public class AlwaysLikeMeme extends AbstractMeme {

    public AlwaysLikeMeme() {
        super(new MemeInfo("always_like",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("我永远喜欢"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 17, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        String text = "我永远喜欢" + name;
        var frame = MemeResources.loadImage("always_like/0.png");

        // 文字层：加粗字体，30-70px 自适应
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(20, 470, frame.getWidth() - 20, 570),
                text, 30f, 70f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var frameWithText = textSurface.getCanvas() != null ? textSurface.makeImageSnapshot() : null;
        // 重绘：先画底 frame，再叠文字
        var baseSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        baseSurface.getCanvas().drawImage(frame, 0, 0);
        baseSurface.getCanvas().drawImage(frameWithText, 0, 0);
        var composed = baseSurface.makeImageSnapshot();

        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var image = ImageEdit.resizeFit(input, 350, 400, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(image, 35, 55);
            return surface.makeImageSnapshot();
        });
    }
}
