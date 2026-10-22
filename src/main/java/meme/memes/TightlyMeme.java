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

/** 移植自 memes/tightly.rs。 */
@Component
public class TightlyMeme extends AbstractMeme {

    public TightlyMeme() {
        super(new MemeInfo("tightly",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("紧贴", "紧紧贴着"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] PARAMS = {
            {39, 169, 267, 141}, {40, 167, 264, 143}, {38, 174, 270, 135}, {40, 167, 264, 143},
            {38, 174, 270, 135}, {40, 167, 264, 143}, {38, 174, 270, 135}, {40, 167, 264, 143},
            {38, 174, 270, 135}, {28, 176, 293, 134}, {5, 215, 333, 96}, {10, 210, 321, 102},
            {3, 210, 330, 104}, {4, 210, 328, 102}, {4, 212, 328, 100}, {4, 212, 328, 100},
            {4, 212, 328, 100}, {4, 212, 328, 100}, {4, 212, 328, 100}, {29, 195, 285, 120}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeFit(images.get(0).image(), 640, 400, ImageEdit.Fit.COVER);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            var frame = MemeResources.loadImage("tightly/%02d.png".formatted(i));
            int[] p = PARAMS[i];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(img, p[2], p[3]);
            canvas.drawImage(resized, p[0], p[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
