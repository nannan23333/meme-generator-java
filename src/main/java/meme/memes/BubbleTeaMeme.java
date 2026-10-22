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

/** 移植自 memes/bubble_tea.rs。 */
@Component
public class BubbleTeaMeme extends AbstractMeme {

    public BubbleTeaMeme() {
        super(new MemeInfo("bubble_tea",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("position", "right", List.of("left", "right", "both"), null, meme.core.ParserFlags.none()))),
                List.of("奶茶"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String position = stringOption(options, "position", "right");
        if (boolOption(options, "left", false)) position = "left";
        if (boolOption(options, "right", false)) position = "right";
        if (boolOption(options, "both", false)) position = "both";
        boolean left = position.equals("left") || position.equals("both");
        boolean right = position.equals("right") || position.equals("both");
        var tea = MemeResources.loadImage("bubble_tea/0.png");
        return InputImage.makePngOrGif(images.get(0), frame -> {
            var resized = ImageEdit.resizeFit(frame, 500, 500, ImageEdit.Fit.COVER);
            var surface = meme.utils.TextRenderer.newSurface(resized.getWidth(), resized.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(resized, 0, 0);
            if (right) {
                canvas.drawImage(tea, 0, 0);
            }
            if (left) {
                canvas.drawImage(ImageEdit.flipHorizontal(tea), 0, 0);
            }
            return surface.makeImageSnapshot();
        });
    }
}
