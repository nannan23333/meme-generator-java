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

/** 移植自 memes/dog_dislike.rs。 */
@Component
public class DogDislikeMeme extends AbstractMeme {

    public DogDislikeMeme() {
        super(new MemeInfo("dog_dislike",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(MemeOption.bool("circle", false))),
                List.of("狗都不玩"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {36, 408}, {36, 410}, {40, 375}, {40, 355}, {36, 325}, {28, 305}, {28, 305},
            {28, 305}, {28, 305}, {28, 285}, {28, 285}, {28, 285}, {28, 285}, {28, 290},
            {30, 295}, {30, 300}, {30, 300}, {30, 300}, {30, 300}, {30, 300}, {30, 300},
            {28, 298}, {26, 296}, {24, 294}, {28, 294}, {26, 294}, {24, 294}, {35, 294},
            {115, 330}, {150, 355}, {180, 420}, {180, 450}, {150, 450}, {150, 450}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        boolean circle = boolOption(options, "circle", false);
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("dog_dislike/%02d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 122, 122);
            if (circle) {
                image = ImageEdit.circle(image);
            }
            int[] loc = LOCS[Math.min(outIdx, LOCS.length - 1)];
            canvas.drawImage(image, loc[0], loc[1]);
            return surface.makeImageSnapshot();
        }, 34, 0.08f);
    }
}
