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

/** 移植自 memes/spider.rs。 */
@Component
public class SpiderMeme extends AbstractMeme {

    public SpiderMeme() {
        super(new MemeInfo("spider",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("蜘蛛", "蜘蛛爬"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[] XS = {
            174, 174, 174, 169, 165, 160, 154, 150, 144, 141, 137, 133, 130, 119, 115, 113, 108, 103,
            103, 97, 91, 85, 87, 79, 74, 79, 75, 75, 78, 79, 77, 77, 70, 81, 93, 94, 104, 110, 119,
            123, 131, 134, 143, 154, 158, 161, 163, 169, 174, 173, 174, 173};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 80, 80);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            var frame = MemeResources.loadImage("spider/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            int jitter = (int) (Math.random() * 3) - 1;
            canvas.drawImage(img, XS[i], 24 + jitter);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.04f);
    }
}
