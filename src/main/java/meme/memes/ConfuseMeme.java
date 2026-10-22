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

/** 移植自 memes/confuse.rs。 */
@Component
public class ConfuseMeme extends AbstractMeme {

    public ConfuseMeme() {
        super(new MemeInfo("confuse",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("迷惑"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 4, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var source = images.get(0).image();
        int imgW = Math.min(source.getWidth(), 500);
        var image = ImageEdit.resizeWidth(source, imgW);
        // 对齐 Rust：make_gif_or_combined_gif，100 帧 / 0.02s
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var surface = meme.utils.TextRenderer.newSurface(image.getWidth(), image.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(image, 0, 0);
            var mask = MemeResources.loadImage(String.format("confuse/%02d.png", outIdx));
            var resized = ImageEdit.resizeFit(mask, image.getWidth(), image.getHeight(), ImageEdit.Fit.COVER);
            canvas.drawImage(resized, 0, 0);
            return surface.makeImageSnapshot();
        }, 100, 0.02f);
    }
}
