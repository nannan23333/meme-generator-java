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

/** 移植自 memes/run_away.rs。 */
@Component
public class RunAwayMeme extends AbstractMeme {

    public RunAwayMeme() {
        super(new MemeInfo("run_away",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("快逃"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var miku = MemeResources.loadImage("run_away/%02d.png".formatted(outIdx));
            int mikuW = miku.getWidth();
            int mikuH = miku.getHeight();
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int imgW = input.getWidth();
            int imgH = input.getHeight();
            float ratio = 1.2f;
            int frameW, frameH;
            if (imgW > imgH) {
                frameH = Math.round(mikuH * ratio);
                frameW = Math.round(frameH * (float) imgW / imgH);
            } else {
                frameW = Math.round(mikuW * ratio);
                frameH = Math.round(frameW * (float) imgH / imgW);
            }
            var img = ImageEdit.resizeFit(input, frameW, frameH, ImageEdit.Fit.COVER);
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(miku, frameW - mikuW, frameH - mikuH);
            return surface.makeImageSnapshot();
        }, 42, 0.03f);
    }
}
