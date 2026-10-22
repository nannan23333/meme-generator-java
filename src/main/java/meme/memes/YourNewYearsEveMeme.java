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

/** 移植自 memes/your_new_years_eve.rs。 */
@Component
public class YourNewYearsEveMeme extends AbstractMeme {

    public YourNewYearsEveMeme() {
        super(new MemeInfo("your_new_years_eve",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("你的跨年"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 31, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 1, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("your_new_years_eve/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeFit(input, 586, 430, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(img, 0, 650);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
