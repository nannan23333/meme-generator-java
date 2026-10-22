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

/** 移植自 memes/flush.rs。 */
@Component
public class FlushMeme extends AbstractMeme {

    public FlushMeme() {
        super(new MemeInfo("flush",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("红温"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 9, 3, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 9, 3, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var image = ImageEdit.square(input);
            int maxW = 282;
            if (image.getWidth() > maxW) {
                image = ImageEdit.resizeExact(image, maxW, maxW);
            }
            int w = image.getWidth();
            int h = image.getHeight();
            if (outIdx >= 18) {
                var frame = MemeResources.loadImage("flush/%02d.png".formatted(outIdx - 18));
                return ImageEdit.resizeExact(frame, w, h);
            }
            float paddingRatio = 0.01f * outIdx;
            float jitterRatio = paddingRatio * 0.4f * ((float) (Math.random() - 0.5));
            int padding = Math.round(w * paddingRatio);
            int jitter = Math.round(w * jitterRatio);
            int alpha = (int) (outIdx * 0.03f * 255);
            var cropped = ImageEdit.crop(image, padding + jitter, padding + jitter,
                    w - padding + jitter, h - padding + jitter);
            var surface = meme.utils.TextRenderer.newSurface(cropped.getWidth(), cropped.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(cropped, 0, 0);
            try (var paint = new io.github.humbleui.skija.Paint()
                    .setColor((alpha << 24) | 0x00FF0000)) {
                canvas.drawRect(io.github.humbleui.types.Rect.makeWH(
                        cropped.getWidth(), cropped.getHeight()), paint);
            }
            return ImageEdit.resizeExact(surface.makeImageSnapshot(), w, h);
        }, 30, 0.08f);
    }
}
