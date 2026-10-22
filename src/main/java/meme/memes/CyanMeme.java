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

/** 移植自 memes/cyan.rs。 */
@Component
public class CyanMeme extends AbstractMeme {

    public CyanMeme() {
        super(new MemeInfo("cyan",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("群青"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int color = 0xFF4E72B8;
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.colorize(ImageEdit.resizeExact(ImageEdit.square(input), 500, 500), color);
            var surface = meme.utils.TextRenderer.newSurface(500, 500);
            var canvas = surface.getCanvas();
            canvas.drawImage(img, 0, 0);
            meme.utils.TextRenderer.drawTextArea(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(400, 40, 480, 280),
                    "群\n青", 80f,
                    meme.utils.TextRenderer.params().fill(0xFFFFFFFF).strokePx(6f, color));
            meme.utils.TextRenderer.drawTextArea(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(200, 270, 480, 350),
                    "YOASOBI", 40f,
                    meme.utils.TextRenderer.params().fill(0xFFFFFFFF)
                            .align(meme.utils.TextRenderer.Params.Align.RIGHT)
                            .strokePx(5f, color));
            return surface.makeImageSnapshot();
        });
    }
}
