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

/** 移植自 memes/nahida_bite.rs。 */
@Component
public class NahidaBiteMeme extends AbstractMeme {

    public NahidaBiteMeme() {
        super(new MemeInfo("nahida_bite",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("纳西妲啃", "草神啃"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {123, 356, 158, 124}, {123, 356, 158, 124}, {123, 355, 158, 125}, {122, 352, 159, 128},
            {122, 350, 159, 130}, {122, 348, 159, 132}, {122, 345, 159, 135}, {121, 343, 160, 137},
            {121, 342, 160, 138}, {121, 341, 160, 139}, {121, 341, 160, 139}, {121, 342, 160, 138},
            {121, 344, 160, 136}, {121, 346, 160, 134}, {122, 349, 159, 131}, {122, 351, 159, 129},
            {122, 353, 159, 127}, {123, 355, 158, 125}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeFit(images.get(0).image(), 160, 140, ImageEdit.Fit.COVER);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 38; i++) {
            var frame = MemeResources.loadImage("nahida_bite/%02d.png".formatted(i));
            int[] loc = LOCS[i % LOCS.length];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var resized = ImageEdit.resizeExact(img, loc[2], loc[3]);
            canvas.drawImage(resized, loc[0], loc[1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
