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

/** 移植自 memes/head_butt.rs。 */
@Component
public class HeadButtMeme extends AbstractMeme {

    public HeadButtMeme() {
        super(new MemeInfo("head_butt",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("顶"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {180, 60, 100, 100}, {184, 75, 100, 100}, {183, 98, 100, 100}, {179, 118, 110, 100},
            {156, 194, 150, 48}, {178, 136, 122, 69}, {175, 66, 122, 85}, {170, 42, 130, 96},
            {175, 34, 118, 95}, {179, 35, 110, 93}, {180, 54, 102, 93}, {183, 58, 97, 92},
            {174, 35, 120, 94}, {179, 35, 109, 93}, {181, 54, 101, 92}, {182, 59, 98, 92},
            {183, 71, 90, 96}, {180, 131, 92, 101}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 58; i++) {
            int index;
            if (i >= 32 && i < 58) index = i - 20;
            else if (i >= 24 && i < 32) index = i - 24;
            else if (i >= 12 && i < 24) index = i - 12;
            else index = i;
            var frame = MemeResources.loadImage("head_butt/%02d.png".formatted(index));
            if (index >= 0 && index < 18) {
                int[] loc = LOCS[index];
                var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                canvas.clear(0xFFFFFFFF);
                var img = ImageEdit.resizeExact(image, loc[2], loc[3]);
                canvas.drawImage(img, loc[0], loc[1]);
                canvas.drawImage(frame, 0, 0);
                frames.add(surface.makeImageSnapshot());
            } else {
                frames.add(frame);
            }
        }
        return GifSupport.writeGifFromSkija(frames, 0.06f);
    }
}
