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

/** anan_say 安安说：移植自 memes/anan_say.rs。 */
@Component
public class AnanSayMeme extends AbstractMeme {

    public AnanSayMeme() {
        super(new MemeInfo("anan_say",
                new MemeParams(0, 0, 1, 1, List.of("吾辈很开心"), List.of(new MemeOption.StringOption("expression", null, List.of("angry", "black", "happy", "shy", "speechless"), null, meme.core.ParserFlags.none()))),
                List.of("安安说"), List.of(), Set.of("夏目安安"),
                OffsetDateTime.of(LocalDateTime.of(2025, 11, 8, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 11, 8, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String[] EXPRESSIONS = {"angry", "black", "happy", "shy", "speechless"};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        String expression = stringOption(options, "expression", null);
        if (expression == null) {
            expression = EXPRESSIONS[(int) (Math.random() * EXPRESSIONS.length)];
        }
        var base = MemeResources.loadImage("anan_say/" + expression + ".png");
        var surface = meme.utils.TextRenderer.newSurface(base.getWidth(), base.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(base, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(105, 445, 412, 625),
                text, 20f, 60f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf").fill(0xFF000000));
        var hand = MemeResources.loadImage("anan_say/hand.png");
        canvas.drawImage(hand, 0, 0);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
