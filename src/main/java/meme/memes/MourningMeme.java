package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/mourning.rs。 */
@Component
public class MourningMeme extends AbstractMeme {

    public MourningMeme() {
        super(new MemeInfo("mourning",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(MemeOption.bool("gray", false))),
                List.of("上香"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 12, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        boolean gray = boolOption(options, "gray", false);
        var frame = MemeResources.loadImage("mourning/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = gray ? ImageEdit.grayscale(input) : input;
            img = ImageEdit.resizeFit(img, 635, 725, ImageEdit.Fit.COVER);
            // 先画头像，再把前景框（香炉白烟、蜡烛）盖上去——顺序反了会盖掉飘到照片前的那缕烟
            canvas.drawImage(img, 645, 145);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
