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

/** 移植自 memes/rub.rs。 */
@Component
public class RubMeme extends AbstractMeme {

    public RubMeme() {
        super(new MemeInfo("rub",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("贴", "贴贴", "蹭", "蹭蹭"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] USER_LOCS = {
            {39, 91, 75, 75}, {49, 101, 75, 75}, {67, 98, 75, 75}, {55, 86, 75, 75},
            {61, 109, 75, 75}, {65, 101, 75, 75}};
    private static final int[][] SELF_LOCS = {
            {102, 95, 70, 80, 0}, {108, 60, 50, 100, 0}, {97, 18, 65, 95, 0}, {65, 5, 75, 75, 20},
            {95, 57, 100, 55, 70}, {109, 107, 65, 75, 0}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var userHead = ImageEdit.circle(images.get(1).image());
        var selfHead = ImageEdit.circle(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            var frame = MemeResources.loadImage("rub/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            int[] ul = USER_LOCS[i];
            canvas.drawImage(ImageEdit.resizeExact(userHead, ul[2], ul[3]), ul[0], ul[1]);
            int[] sl = SELF_LOCS[i];
            canvas.drawImage(ImageEdit.rotate(ImageEdit.resizeExact(selfHead, sl[2], sl[3]), sl[4]), sl[0], sl[1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
