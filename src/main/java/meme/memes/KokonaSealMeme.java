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

/** 移植自 memes/kokona_seal.rs。 */
@Component
public class KokonaSealMeme extends AbstractMeme {

    public KokonaSealMeme() {
        super(new MemeInfo("kokona_seal",
                new MemeParams(0, 0, 1, 1, List.of("满分") , List.of(new MemeOption.IntegerOption("number", null, 1, 12, null, meme.core.ParserFlags.none()))),
                List.of("心奈印章"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 11, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        int num = 1 + (int) (Math.random() * 12);
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = Math.max(1, Math.min(12, n.intValue()));
        }
        var textSurface = meme.utils.TextRenderer.newSurface(320, 155);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(10, 10, 310, 145),
                text, 50f, 150f,
                meme.utils.TextRenderer.params().fill(0xFFFE0000).fontFamilies("FZShaoEr-M11S"));
        var textImage = ImageEdit.rotate(textSurface.makeImageSnapshot(), -16f);
        var frame = MemeResources.loadImage("kokona_seal/%02d.png".formatted(num));
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 75, 25);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
