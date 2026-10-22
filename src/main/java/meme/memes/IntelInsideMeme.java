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

/** 移植自 memes/intel_inside.rs。 */
@Component
public class IntelInsideMeme extends AbstractMeme {

    public IntelInsideMeme() {
        super(new MemeInfo("intel_inside",
                new MemeParams(0, 0, 1, 1, List.of("intel"), List.of()),
                List.of("inside"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("intel_inside/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(240, 340, 680, 580),
                text, 80f, 180f,
                meme.utils.TextRenderer.params().fontFamilies("NeoSans-Bold.ttf").fill(0xFFFFFFFF));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
