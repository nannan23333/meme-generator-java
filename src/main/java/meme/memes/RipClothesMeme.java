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

/** 移植自 memes/rip_clothes.rs。 */
@Component
public class RipClothesMeme extends AbstractMeme {

    public RipClothesMeme() {
        super(new MemeInfo("rip_clothes",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("撕衣服"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 24, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var img = ImageEdit.resizeFit(images.get(0).image(), 480, 270, ImageEdit.Fit.COVER);
            if (outIdx <= 15) {
                var frame = MemeResources.loadImage("rip_clothes/%02d.png".formatted(outIdx));
                var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                canvas.clear(0xFFFFFFFF);
                canvas.drawImage(img, 0, 0);
                canvas.drawImage(frame, 0, 0);
                return surface.makeImageSnapshot();
            }
            return img;
        }, 20, 0.1f);
    }
}
