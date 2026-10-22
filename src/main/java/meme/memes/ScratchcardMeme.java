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

/** 移植自 memes/scratchcard.rs。 */
@Component
public class ScratchcardMeme extends AbstractMeme {

    public ScratchcardMeme() {
        super(new MemeInfo("scratchcard",
                new MemeParams(0, 0, 1, 1, List.of("谢谢参与"), List.of()),
                List.of("刮刮乐"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 23, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 23, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("scratchcard/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(80, 160, 360, 290),
                text, 30f, 80f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        var mask = MemeResources.loadImage("scratchcard/1.png");
        canvas.drawImage(mask, 0, 0);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
