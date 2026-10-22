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

/** 移植自 memes/kaleidoscope.rs。 */
@Component
public class KaleidoscopeMeme extends AbstractMeme {

    public KaleidoscopeMeme() {
        super(new MemeInfo("kaleidoscope",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(MemeOption.bool("circle", false))),
                List.of("万花筒", "万花镜"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        boolean circle = boolOption(options, "circle", false);
        int circleNum = 10;
        int imgPerCircle = 4;
        float angleStep = 360f / imgPerCircle;
        float cx = circleNum * 50f + 100f;
        float cy = cx;
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface((int) (cx * 2), (int) (cy * 2));
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            float currentAngle = 0f;
            for (int i = 0; i < circleNum; i++) {
                float r = i * 50f + 100f;
                int imgW = i * 35 + 100;
                var img = ImageEdit.resizeWidth(input, imgW);
                if (circle) {
                    img = ImageEdit.circle(img);
                }
                for (int j = 0; j < imgPerCircle; j++) {
                    float angle = currentAngle + angleStep * j;
                    var imgRot = ImageEdit.rotate(img, 90f - angle);
                    double rad = Math.toRadians(angle);
                    // Rust: y 用减号、坐标四舍五入
                    int x = Math.round((float) (cx + r * Math.cos(rad) - imgRot.getWidth() / 2f));
                    int y = Math.round((float) (cy - r * Math.sin(rad) - imgRot.getHeight() / 2f));
                    canvas.drawImage(imgRot, x, y);
                }
                currentAngle += angleStep / 2f;
            }
            return surface.makeImageSnapshot();
        });
    }
}
