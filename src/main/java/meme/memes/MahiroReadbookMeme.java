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

/** 移植自 memes/mahiro_readbook.rs。 */
@Component
public class MahiroReadbookMeme extends AbstractMeme {

    public MahiroReadbookMeme() {
        super(new MemeInfo("mahiro_readbook",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("真寻看书"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 19, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 19, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] POSITIONS = {
            {0, 118}, {0, 117}, {0, 116}, {0, 116}, {-3, 116}, {-7, 117}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.perspective(
                ImageEdit.resizeFit(images.get(0).image(), 70, 100, ImageEdit.Fit.COVER),
                0, 6, 77, -5, 100, 100, 32, 100);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 48; i++) {
            var frame = MemeResources.loadImage("mahiro_readbook/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(240, 240);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            int idx = Math.max(0, Math.min(5, i - 16));
            canvas.drawImage(img, POSITIONS[idx][0], POSITIONS[idx][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
