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

/** 移植自 memes/luotianyi_need.rs。 */
@Component
public class LuotianyiNeedMeme extends AbstractMeme {

    public LuotianyiNeedMeme() {
        super(new MemeInfo("luotianyi_need",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("洛天依要", "天依要"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("luotianyi_need/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var img = ImageEdit.resizeFit(input, 195, 195, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(img, 43, 146);
            return surface.makeImageSnapshot();
        });
    }
}
