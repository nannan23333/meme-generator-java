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

/** 移植自 memes/raise_sign.rs。 */
@Component
public class RaiseSignMeme extends AbstractMeme {

    public RaiseSignMeme() {
        super(new MemeInfo("raise_sign",
                new MemeParams(0, 0, 1, 1, List.of("大佬带带我") , List.of()),
                List.of("举牌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var textSurface = meme.utils.TextRenderer.newSurface(360, 260);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(10, 10, 350, 250),
                text, 30f, 80f,
                meme.utils.TextRenderer.params().fill(0xFF51201B).fontFamilies("FZShaoEr-M11S"));
        var textImage = ImageEdit.perspective(textSurface.makeImageSnapshot(),
                33, 0, 375, 120, 333, 387, 0, 258);
        var frame = MemeResources.loadImage("raise_sign/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 285, 24);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
