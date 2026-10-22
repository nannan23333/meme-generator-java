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

/** 移植自 memes/alike.rs。 */
@Component
public class AlikeMeme extends AbstractMeme {

    public AlikeMeme() {
        super(new MemeInfo("alike",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("一样"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 1, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 22, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var baseSurface = meme.utils.TextRenderer.newSurface(470, 180);
        var bcanvas = baseSurface.getCanvas();
        bcanvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextArea(bcanvas,
                io.github.humbleui.types.Rect.makeLTRB(10, 30, 185, 160),
                "你怎么跟", 40f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.RIGHT));
        meme.utils.TextRenderer.drawTextArea(bcanvas,
                io.github.humbleui.types.Rect.makeLTRB(365, 30, 460, 160),
                "一样", 40f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.LEFT));
        var frame = baseSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(470, 180);
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.resizeFit(input, 150, 150, ImageEdit.Fit.COVER);
            canvas.drawImage(image, 200, 15);
            return surface.makeImageSnapshot();
        });
    }
}
