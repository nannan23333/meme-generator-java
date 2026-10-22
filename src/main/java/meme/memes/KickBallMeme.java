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

/** 移植自 memes/kick_ball.rs。 */
@Component
public class KickBallMeme extends AbstractMeme {

    public KickBallMeme() {
        super(new MemeInfo("kick_ball",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("踢球"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {57, 136}, {56, 117}, {55, 99}, {52, 113}, {50, 126}, {48, 139}, {47, 112},
            {47, 85}, {47, 57}, {48, 97}, {50, 136}, {51, 176}, {52, 169}, {55, 181}, {58, 153}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 78, 78);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            var frame = MemeResources.loadImage("kick_ball/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var rotated = ImageEdit.rotateCrop(image, 24f * i);
            canvas.drawImage(rotated, LOCS[i][0], LOCS[i][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
