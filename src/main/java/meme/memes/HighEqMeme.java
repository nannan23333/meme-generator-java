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

/** 移植自 memes/high_eq.rs。 */
@Component
public class HighEqMeme extends AbstractMeme {

    public HighEqMeme() {
        super(new MemeInfo("high_eq",
                new MemeParams(0, 0, 2, 2, List.of("高情商", "低情商") , List.of()),
                List.of("高低情商", "低高情商"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("high_eq/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(40, 540, 602, 1140),
                texts.get(0), 50f, 100f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.05f, 0xFF000000));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(682, 540, 1244, 1140),
                texts.get(1), 50f, 100f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).stroke(0.05f, 0xFF000000));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
