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

/** 移植自 memes/sekaiichi_kawaii.rs。 */
@Component
public class SekaiichiKawaiiMeme extends AbstractMeme {

    public SekaiichiKawaiiMeme() {
        super(new MemeInfo("sekaiichi_kawaii",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("世界第一可爱"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = images.get(0).image();
        boolean wide = img.getWidth() / (float) img.getHeight() > 1.155f;
        var frame = MemeResources.loadImage(wide ? "sekaiichi_kawaii/0.png" : "sekaiichi_kawaii/1.png");
        int w = wide ? 810 : 585;
        int h = wide ? 416 : 810;
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeFit(input, w, h, ImageEdit.Fit.COVER);
            canvas.drawImage(resized, 45, 45);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
