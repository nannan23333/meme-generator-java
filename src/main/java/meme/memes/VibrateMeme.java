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

/** 移植自 memes/vibrate.rs。 */
@Component
public class VibrateMeme extends AbstractMeme {

    public VibrateMeme() {
        super(new MemeInfo("vibrate",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("震动"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 8, 28, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 8, 28, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var img = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int w = img.getWidth();
            int h = img.getHeight();
            int[][] locs = {{0, 0}, {w / 25, h / 25}, {w / 50, h / 50}, {0, h / 25}, {w / 25, 0}};
            var surface = meme.utils.TextRenderer.newSurface(w + w / 25, h + h / 25);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(img, locs[outIdx][0], locs[outIdx][1]);
            return surface.makeImageSnapshot();
        }, 5, 0.05f);
    }
}
