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

/** 移植自 memes/play_basketball.rs。 */
@Component
public class PlayBasketballMeme extends AbstractMeme {

    public PlayBasketballMeme() {
        super(new MemeInfo("play_basketball",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("打篮球", "火柴人打篮球"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 4, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 4, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    // 每项 {x, y, angle} 或 null
    private static final float[][] LOCS = {
            {297, 321, 0.0f},
            {300, 327, 7.2f},
            {308, 410, 5.0f},
            {308, 412, 5.0f},
            {301, 355, 0.0f},
            {296, 317, 0.0f},
            {296, 320, 0.0f},
            {296, 320, 0.0f},
            {352, 435, 0.0f},
            null,
            null,
            null,
            null,
            {175, 343, 5.0f},
            {173, 353, 5.0f},
            {173, 353, 2.0f},
            {171, 354, 2.0f},
            {189, 158, 0.0f},
            {213, 149, 0.0f},
            {238, 139, 0.0f},
            {245, 139, 0.0f},
            {252, 139, -12.0f},
            {257, 142, -12.0f},
            {261, 142, -17.0f},
            {265, 145, -17.0f},
            {271, 148, -18.0f},
            {279, 156, -18.0f},
            {286, 163, -25.0f},
            {287, 160, -25.0f},
            {289, 159, -27.0f},
            {286, 165, -27.0f},
            {285, 167, -20.0f},
            {285, 179, -20.0f},
            {282, 192, 25.0f},
            {284, 219, 25.0f},
            {280, 242, 32.0f},
            {283, 280, 32.0f},
            {287, 315, 32.0f}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeExact(ImageEdit.circle(input), 77, 77);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 38; i++) {
            var frame = MemeResources.loadImage("play_basketball/%02d.png".formatted(i));
            float[] loc = LOCS[i];
            if (loc != null) {
                var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                var rotated = ImageEdit.rotate(img, loc[2]);
                canvas.drawImage(rotated, Math.round(loc[0] - rotated.getWidth() / 2f),
                        Math.round(loc[1] - rotated.getHeight() / 2f));
                canvas.drawImage(frame, 0, 0);
                frames.add(surface.makeImageSnapshot());
            } else {
                frames.add(frame);
            }
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
