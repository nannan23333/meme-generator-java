package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
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

/** add_chaos 添乱：移植自 memes/add_chaos.rs。 */
@Component
public class AddChaosMeme extends AbstractMeme {

    public AddChaosMeme() {
        super(new MemeInfo("add_chaos",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("添乱", "给社会添乱"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 21, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var banner = MemeResources.loadImage("add_chaos/0.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var image = ImageEdit.resizeWidth(input, 240);
            var surface = meme.utils.TextRenderer.newSurface(image.getWidth(), image.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(image, 0, 0);
            canvas.drawImage(banner, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
