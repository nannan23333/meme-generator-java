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

/** 移植自 memes/captain.rs。 */
@Component
public class CaptainMeme extends AbstractMeme {

    public CaptainMeme() {
        super(new MemeInfo("captain",
                new MemeParams(2, 5, 0, 0, List.of(), List.of()),
                List.of("舰长"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var bg0 = MemeResources.loadImage("captain/0.png");
        var bg1 = MemeResources.loadImage("captain/1.png");
        var bg2 = MemeResources.loadImage("captain/2.png");
        int imageNum = images.size();
        int iterNum = imageNum == 2 ? 3 : imageNum;
        var surface = meme.utils.TextRenderer.newSurface(640, 440 * iterNum);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        for (int i = 0; i < iterNum; i++) {
            var bg = i == iterNum - 1 ? bg2 : (i == iterNum - 2 ? bg1 : bg0);
            var img = i >= imageNum ? images.get(imageNum - 1).image() : images.get(i).image();
            var image = ImageEdit.resizeFit(img, 250, 350, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(bg, 0, 440 * i);
            canvas.drawImage(image, 350, 35 + 440 * i);
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
