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

/** 移植自 memes/erised_mirror.rs。 */
@Component
public class ErisedMirrorMeme extends AbstractMeme {

    public ErisedMirrorMeme() {
        super(new MemeInfo("erised_mirror",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("意若思镜"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 11, 25, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 11, 25, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("erised_mirror/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.perspective(
                    ImageEdit.brightness(
                            ImageEdit.colorize(ImageEdit.resizeFit(input, 360, 207, ImageEdit.Fit.COVER), 0xFF394E7D),
                            0.7f),
                    0, 0, 360, 0, 367, 207, 7, 207);
            canvas.drawImage(img, 55, 578);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
