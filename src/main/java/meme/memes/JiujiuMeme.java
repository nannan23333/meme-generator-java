package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
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

/** 移植自 memes/jiujiu.rs。 */
@Component
public class JiujiuMeme extends AbstractMeme {

    public JiujiuMeme() {
        super(new MemeInfo("jiujiu",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("啾啾"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 18, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.resizeFit(images.get(0).image(), 75, 51, ImageEdit.Fit.COVER);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            var frame = MemeResources.loadImage("jiujiu/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(image, 0, 0);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.06f);
    }
}
