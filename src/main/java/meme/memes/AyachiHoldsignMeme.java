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

/** 移植自 memes/ayachi_holdsign.rs。 */
@Component
public class AyachiHoldsignMeme extends AbstractMeme {

    public AyachiHoldsignMeme() {
        super(new MemeInfo("ayachi_holdsign",
                new MemeParams(0, 0, 1, 1, List.of("我控制不住自己啊") , List.of()),
                List.of("宁宁举牌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 26, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var textSurface = meme.utils.TextRenderer.newSurface(600, 350);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(20, 20, 580, 330),
                text, 80f, 150f,
                meme.utils.TextRenderer.params().fill(0xFF51201B).align(meme.utils.TextRenderer.Params.Align.CENTER)
                        .fontFamilies("FZShaoEr-M11S"));
        var textImage = ImageEdit.perspective(textSurface.makeImageSnapshot(),
                0, 235, 523, 0, 659, 297, 170, 536);
        var frame = MemeResources.loadImage("ayachi_holdsign/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 125, 307);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
