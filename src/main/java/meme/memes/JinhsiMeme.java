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

/** 移植自 memes/jinhsi.rs。 */
@Component
public class JinhsiMeme extends AbstractMeme {

    public JinhsiMeme() {
        super(new MemeInfo("jinhsi",
                new MemeParams(0, 0, 1, 1, List.of("汐汐"), List.of(new MemeOption.IntegerOption("number", null, 1, 13, null, meme.core.ParserFlags.none()))),
                List.of("汐汐", "今汐"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 21, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[] PADDINGS = {55, 43, 50, 36, 40, 33, 36, 38, 33, 46, 26, 33, 28};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        int num = 1 + (int) (Math.random() * 13);
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = Math.max(1, Math.min(13, n.intValue()));
        }
        var frame = MemeResources.loadImage("jinhsi/%02d.png".formatted(num));
        int padding = PADDINGS[num - 1];
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(0, frame.getHeight() - padding, frame.getWidth(), frame.getHeight()),
                text, 15f, 50f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
