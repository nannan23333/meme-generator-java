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

/** 移植自 memes/jiubingfufa.rs。 */
@Component
public class JiubingfufaMeme extends AbstractMeme {

    public JiubingfufaMeme() {
        super(new MemeInfo("jiubingfufa",
                new MemeParams(1, 1, 0, 1, List.of("此乃旧病复发也"), List.of()),
                List.of("旧病复发"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 11, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "此乃旧病复发也" : texts.get(0);
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 120, 120);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            var frame = MemeResources.loadImage("jiubingfufa/%02d.jpg".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(img, 32, frame.getHeight() - 162);
            if (i > 9) {
                meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                        canvas,
                        io.github.humbleui.types.Rect.makeLTRB(0, 0, 290, 160),
                        text, 20f, 32f, meme.utils.TextRenderer.params()
                                .fill(0xFFFFFFFF).strokePx(3f, 0xFF000000));
            }
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.06f);
    }
}
