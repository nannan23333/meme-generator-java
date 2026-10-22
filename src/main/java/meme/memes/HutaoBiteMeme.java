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

/** 移植自 memes/hutao_bite.rs。 */
@Component
public class HutaoBiteMeme extends AbstractMeme {

    public HutaoBiteMeme() {
        super(new MemeInfo("hutao_bite",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("胡桃啃"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 25, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {{98, 101, 108, 234}, {96, 100, 108, 237}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            var frame = MemeResources.loadImage("hutao_bite/%d.png".formatted(i));
            int[] loc = LOCS[i];  // (w, h, x, y)
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            // 对齐 Rust：先头像后 frame（frame 是前景遮罩）
            var resized = ImageEdit.resizeExact(image, loc[0], loc[1]);
            canvas.drawImage(resized, loc[2], loc[3]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
