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

/** 移植自 memes/sold_out.rs。 */
@Component
public class SoldOutMeme extends AbstractMeme {

    public SoldOutMeme() {
        super(new MemeInfo("sold_out",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("卖掉了"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var icon = MemeResources.loadImage("sold_out/0.png");
        return InputImage.makePngOrGif(images.get(0), img -> {
            var resized = img.getWidth() > img.getHeight()
                    ? ImageEdit.resizeHeight(img, 600) : ImageEdit.resizeWidth(img, 600);
            var surface = meme.utils.TextRenderer.newSurface(resized.getWidth(), resized.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(resized, 0, 0);
            try (var paint = new io.github.humbleui.skija.Paint().setColor(0x50000000)) {
                canvas.drawRect(io.github.humbleui.types.Rect.makeWH(resized.getWidth(), resized.getHeight()), paint);
            }
            canvas.drawImage(icon,
                    (resized.getWidth() - icon.getHeight()) / 2,
                    (resized.getHeight() - icon.getHeight()) / 2);
            return surface.makeImageSnapshot();
        });
    }
}
