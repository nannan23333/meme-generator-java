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

/** 移植自 memes/fencing.rs。 */
@Component
public class FencingMeme extends AbstractMeme {

    public FencingMeme() {
        super(new MemeInfo("fencing",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("击剑", "🤺"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] USER_LOCS = {
            {57, 4}, {55, 5}, {58, 7}, {57, 5}, {53, 8}, {54, 9}, {64, 5}, {66, 8}, {70, 9},
            {73, 8}, {81, 10}, {77, 10}, {72, 4}, {79, 8}, {50, 8}, {60, 7}, {67, 6}, {60, 6}, {50, 9}};
    private static final int[][] SELF_LOCS = {
            {10, 6}, {3, 6}, {32, 7}, {22, 7}, {13, 4}, {21, 6}, {30, 6}, {22, 2}, {22, 3},
            {26, 8}, {23, 8}, {27, 10}, {30, 9}, {17, 6}, {12, 8}, {11, 7}, {8, 6}, {-2, 10}, {4, 9}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var selfHead = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 27, 27);
        var userHead = ImageEdit.resizeExact(ImageEdit.circle(images.get(1).image()), 27, 27);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            var frame = MemeResources.loadImage("fencing/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(userHead, USER_LOCS[i][0], USER_LOCS[i][1]);
            canvas.drawImage(selfHead, SELF_LOCS[i][0], SELF_LOCS[i][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
