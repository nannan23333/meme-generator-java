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

/** 移植自 memes/run.rs。 */
@Component
public class RunMeme extends AbstractMeme {

    public RunMeme() {
        super(new MemeInfo("run",
                new MemeParams(0, 0, 1, 1, List.of("快跑"), List.of()),
                List.of("快跑"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var textSurface = meme.utils.TextRenderer.newSurface(122, 53);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeXYWH(0, 0, 122, 53),
                text, 10f, 50f, meme.utils.TextRenderer.params());
        var textImage = ImageEdit.rotate(textSurface.makeImageSnapshot(), -7f);

        var frame = MemeResources.loadImage("run/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 200, 195);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
