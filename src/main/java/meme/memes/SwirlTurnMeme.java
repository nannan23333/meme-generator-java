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

/** 移植自 memes/swirl_turn.rs。 */
@Component
public class SwirlTurnMeme extends AbstractMeme {

    public SwirlTurnMeme() {
        super(new MemeInfo("swirl_turn",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("回旋转", "旋风转"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int frameNum = 40;
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var surface = meme.utils.TextRenderer.newSurface(300, 300);
            var canvas = surface.getCanvas();
            var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 100, 100);
            float startAngle = outIdx * 360f / frameNum;
            int num = 24;
            for (int j = 0; j < num; j++) {
                float angle = startAngle + j * 360f / num;
                double rad = Math.toRadians(angle);
                int x = (int) Math.round(150 + 75 * Math.cos(rad));
                int y = (int) Math.round(150 + 75 * Math.sin(rad));
                canvas.drawImage(img, x - 50, y - 50);
            }
            return surface.makeImageSnapshot();
        }, frameNum, 0.02f);
    }
}
