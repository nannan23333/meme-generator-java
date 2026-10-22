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

/** 移植自 memes/overtime.rs。 */
@Component
public class OvertimeMeme extends AbstractMeme {

    public OvertimeMeme() {
        super(new MemeInfo("overtime",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("加班"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 1, 8, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("overtime/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.rotate(ImageEdit.resizeFit(input, 250, 250, ImageEdit.Fit.COVER), 25f);
            // frame 是前景（漫画格线/气泡），必须最后画，否则头像会溢出格子边界
            canvas.drawImage(img, 165, 220);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
