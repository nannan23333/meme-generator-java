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

/** 移植自 memes/pass_the_buck.rs。 */
@Component
public class PassTheBuckMeme extends AbstractMeme {

    public PassTheBuckMeme() {
        super(new MemeInfo("pass_the_buck",
                new MemeParams(1, 1, 0, 1, List.of("你写!") , List.of()),
                List.of("推锅", "甩锅"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 31, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {2, 26}, {10, 24}, {15, 27}, {17, 29}, {10, 20}, {2, 29}, {3, 31}, {1, 30}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 27, 27);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            var frame = MemeResources.loadImage("pass_the_buck/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            if (!texts.isEmpty()) {
                meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                        canvas,
                        io.github.humbleui.types.Rect.makeLTRB(0, 0, frame.getWidth(), 20),
                        texts.get(0), 10f, 20f, meme.utils.TextRenderer.params());
            }
            canvas.drawImage(img, LOCS[i][0], LOCS[i][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
