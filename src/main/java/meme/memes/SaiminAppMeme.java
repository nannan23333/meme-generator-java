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

/** 移植自 memes/saimin_app.rs。 */
@Component
public class SaiminAppMeme extends AbstractMeme {

    public SaiminAppMeme() {
        super(new MemeInfo("saimin_app",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("催眠app"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var app = MemeResources.loadImage("saimin_app/%02d.png".formatted(outIdx));
            int appW = app.getWidth();
            int appH = app.getHeight();
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int imgW = input.getWidth();
            int imgH = input.getHeight();
            int frameW, frameH;
            if (imgW > imgH) {
                frameW = appH * imgW / imgH;
                frameH = appH;
            } else {
                frameW = appW;
                frameH = appW * imgH / imgW;
            }
            var img = ImageEdit.resizeFit(input, frameW, frameH, ImageEdit.Fit.COVER);
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(app, 0, frameH - appH);
            return surface.makeImageSnapshot();
        }, 25, 0.03f);
    }
}
