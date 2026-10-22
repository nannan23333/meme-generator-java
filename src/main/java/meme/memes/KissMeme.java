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

/** 移植自 memes/kiss.rs。 */
@Component
public class KissMeme extends AbstractMeme {

    public KissMeme() {
        super(new MemeInfo("kiss",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("亲", "亲亲"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] USER_LOCS = {
            {58, 90}, {62, 95}, {42, 100}, {50, 100}, {56, 100}, {18, 120}, {28, 110},
            {54, 100}, {46, 100}, {60, 100}, {35, 115}, {20, 120}, {40, 96}};
    private static final int[][] SELF_LOCS = {
            {92, 64}, {135, 40}, {84, 105}, {39, 27}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var selfHead = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 40, 40);
        var userHead = ImageEdit.resizeExact(ImageEdit.circle(images.get(1).image()), 50, 50);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            var frame = MemeResources.loadImage("kiss/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(userHead, USER_LOCS[i][0], USER_LOCS[i][1]);
            canvas.drawImage(selfHead, SELF_LOCS[Math.min(i, 3)][0], SELF_LOCS[Math.min(i, 3)][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
