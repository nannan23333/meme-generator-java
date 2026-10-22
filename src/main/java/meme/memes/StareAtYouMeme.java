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

/** 移植自 memes/stare_at_you.rs。 */
@Component
public class StareAtYouMeme extends AbstractMeme {

    public StareAtYouMeme() {
        super(new MemeInfo("stare_at_you",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("盯着你"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var baseSurface = meme.utils.TextRenderer.newSurface(400, 400);
        var bcanvas = baseSurface.getCanvas();
        bcanvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(bcanvas,
                io.github.humbleui.types.Rect.makeLTRB(0, 0, 150, 100), "我雇了一只", 25f, 25f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf").align(meme.utils.TextRenderer.Params.Align.RIGHT));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(bcanvas,
                io.github.humbleui.types.Rect.makeLTRB(250, 0, 400, 100), "来盯着👁你", 25f, 25f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var frame = baseSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(400, 400);
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var img = ImageEdit.resizeFit(input, 400, 300, ImageEdit.Fit.COVER);
            var thumbnail = ImageEdit.resizeFit(input, 80, 60, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 0, 100);
            canvas.drawImage(thumbnail, 160, 20);
            return surface.makeImageSnapshot();
        });
    }
}
