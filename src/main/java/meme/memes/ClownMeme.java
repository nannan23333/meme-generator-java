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

/** 移植自 memes/clown.rs。 */
@Component
public class ClownMeme extends AbstractMeme {

    public ClownMeme() {
        super(new MemeInfo("clown",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(MemeOption.bool("person", false))),
                List.of("小丑"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        boolean person = boolOption(options, "person", false);
        String framePath = person ? "clown/person.png" : "clown/circle.png";
        int w = person ? 434 : 554;
        int h = person ? 467 : 442;
        float angle = 26f;
        int leftCenterX = person ? 174 : 153;
        int centerY = person ? 378 : 341;
        return InputImage.makePngOrGif(images.get(0), input -> {
            var frame = MemeResources.loadImage(framePath);
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, w, h, ImageEdit.Fit.COVER);
            var leftPart = ImageEdit.rotate(ImageEdit.crop(image, 0, 0, image.getWidth() / 2, image.getHeight()), -angle);
            var rightPart = ImageEdit.rotate(ImageEdit.crop(image, image.getWidth() / 2, 0, image.getWidth(), image.getHeight()), angle);
            int leftTopX = leftCenterX - leftPart.getWidth() / 2;
            int topY = centerY - leftPart.getHeight() / 2;
            int rightTopX = frame.getWidth() - leftTopX - rightPart.getWidth();
            canvas.drawImage(leftPart, leftTopX, topY);
            canvas.drawImage(rightPart, rightTopX, topY);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
