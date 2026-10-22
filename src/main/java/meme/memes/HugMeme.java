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

/** 移植自 memes/hug.rs。 */
@Component
public class HugMeme extends AbstractMeme {

    public HugMeme() {
        super(new MemeInfo("hug",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("抱", "抱抱"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 4, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] USER_LOCS = {
            {108, 15}, {107, 14}, {104, 16}, {102, 14}, {104, 15}, {108, 15},
            {108, 15}, {103, 16}, {102, 15}, {104, 14}};
    private static final int[][] SELF_LOCS = {
            {78, 120}, {115, 130}, {115, 130}, {110, 100}, {80, 100}, {75, 115},
            {105, 127}, {105, 127}, {110, 98}, {80, 105}};
    private static final int[] ANGLES = {48, 18, 0, -38, -31, 43, 22, 0, -34, -35};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var selfHead = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 120, 120);
        var userHead = ImageEdit.resizeExact(ImageEdit.square(images.get(1).image()), 105, 105);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var frame = MemeResources.loadImage("hug/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(userHead, USER_LOCS[i][0], USER_LOCS[i][1]);
            canvas.drawImage(ImageEdit.rotate(selfHead, ANGLES[i]), SELF_LOCS[i][0], SELF_LOCS[i][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
