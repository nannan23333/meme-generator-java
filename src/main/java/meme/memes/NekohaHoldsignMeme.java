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

/** 移植自 memes/nekoha_holdsign.rs。 */
@Component
public class NekohaHoldsignMeme extends AbstractMeme {

    public NekohaHoldsignMeme() {
        super(new MemeInfo("nekoha_holdsign",
                new MemeParams(0, 0, 1, 1, List.of("V我50"), List.of()),
                List.of("猫羽雫举牌", "猫猫举牌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 8, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 8, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("nekoha_holdsign/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(210, 520, 570, 765),
                text, 25f, 65f,
                meme.utils.TextRenderer.params().fill(0xFF486EAD)
                        .fontFamilies("FZShaoEr-M11S"));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
