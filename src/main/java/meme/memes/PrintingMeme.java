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

/** 移植自 memes/printing.rs。 */
@Component
public class PrintingMeme extends AbstractMeme {

    public PrintingMeme() {
        super(new MemeInfo("printing",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("打印"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeFit(images.get(0).image(), 304, 174, ImageEdit.Fit.CONTAIN);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 115; i++) {
            var frame = MemeResources.loadImage("printing/%03d.png".formatted(i));
            if (i >= 50 && i < 115) {
                var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                canvas.clear(0xFFFFFFFF);
                canvas.drawImage(img, 298 - img.getWidth() / 2, 339 - img.getHeight());
                canvas.drawImage(frame, 0, 0);
                frames.add(surface.makeImageSnapshot());
            } else {
                frames.add(frame);
            }
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
