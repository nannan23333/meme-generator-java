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

/** 移植自 memes/telescope.rs。 */
@Component
public class TelescopeMeme extends AbstractMeme {

    public TelescopeMeme() {
        super(new MemeInfo("telescope",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("望远镜"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var img = ImageEdit.square(images.get(0).image());
            var imgBig = ImageEdit.resizeExact(img, 600, 600);
            var imgSmall = ImageEdit.resizeExact(img, 230, 230);
            var frame = MemeResources.loadImage("telescope/%02d.png".formatted(outIdx));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            if (outIdx >= 4 && outIdx <= 17) {
                canvas.drawImage(imgBig, -167 + (outIdx - 4) * 4, -361 + (outIdx - 4) * 7);
            } else if (outIdx >= 23 && outIdx <= 37) {
                canvas.drawImage(imgBig, -90 + (outIdx - 23) * 5, -245 + (outIdx - 23) * 5);
            } else if (outIdx >= 43 && outIdx <= 45) {
                canvas.drawImage(imgBig, 0, -210);
            } else if (outIdx >= 46 && outIdx <= 56) {
                canvas.drawImage(imgSmall, 8, -21);
            }
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 69, 0.1f);
    }
}
