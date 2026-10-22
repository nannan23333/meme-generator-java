package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/mihoyo.rs。 */
@Component
public class MihoyoMeme extends AbstractMeme {

    public MihoyoMeme() {
        super(new MemeInfo("mihoyo",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("米哈游"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var logo = ImageEdit.resizeHeight(MemeResources.loadImage("mihoyo/0.png"), 50);
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.resizeExact(ImageEdit.square(input), 500, 500);
            var surface = meme.utils.TextRenderer.newSurface(500, 500);
            var canvas = surface.getCanvas();
            canvas.drawImage(img, 0, 0);
            try (var paint = new io.github.humbleui.skija.Paint().setColor(0xE6353141)) {
                canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(0, 440, 500, 60), paint);
            }
            canvas.drawImage(logo, (img.getWidth() - logo.getWidth()) / 2, 445);
            return ImageEdit.roundCorner(surface.makeImageSnapshot(), 100f);
        });
    }
}
