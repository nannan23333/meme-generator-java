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

/** 移植自 memes/clown_mask.rs。 */
@Component
public class ClownMaskMeme extends AbstractMeme {

    public ClownMaskMeme() {
        super(new MemeInfo("clown_mask",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("mode", "front", List.of("front", "behind"), null, meme.core.ParserFlags.none()))),
                List.of("小丑面具"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String mode = stringOption(options, "mode", null);
        if (boolOption(options, "front", false)) mode = "front";
        if (boolOption(options, "behind", false)) mode = "behind";
        if (mode == null) mode = "front";
        if (mode.equals("front")) {
            var frame = MemeResources.loadImage("clown_mask/0.png");
            return InputImage.makePngOrGif(images.get(0), input -> {
                var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                var image = ImageEdit.rotateCrop(
                        ImageEdit.resizeExact(ImageEdit.circle(input), 440, 440), -15f);
                canvas.drawImage(image, 16, 104);
                canvas.drawImage(frame, 0, 0);
                return surface.makeImageSnapshot();
            });
        }
        var frame1 = MemeResources.loadImage("clown_mask/1.png");
        var frame2 = MemeResources.loadImage("clown_mask/2.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame1.getWidth(), frame1.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame1, 0, 0);
            var image = ImageEdit.rotateCrop(
                    ImageEdit.perspective(ImageEdit.circle(input), 282, 0, 496, 154, 214, 546, 0, 392), -6f);
            canvas.drawImage(image, 214, 100);
            canvas.drawImage(frame2, -85, 20);
            return surface.makeImageSnapshot();
        });
    }
}
