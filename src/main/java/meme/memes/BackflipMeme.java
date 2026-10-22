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

/** 移植自 memes/backflip.rs。 */
@Component
public class BackflipMeme extends AbstractMeme {

    public BackflipMeme() {
        super(new MemeInfo("backflip",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("后空翻"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img0 = images.get(0).image();
        int imgW = img0.getWidth();
        int imgH = img0.getHeight();
        double length = Math.sqrt((double) imgW * imgW + imgH * imgH);
        int frameW = (int) (length * 1.3);
        float bounceH = imgH * 1.2f;
        int frameH = (int) (bounceH + length / 2 + imgH * 0.6);
        float centerX = frameW / 2f;
        float groundY = frameH - imgH / 2f;
        int totalFrames = 30;
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var img = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            float t = outIdx / (float) totalFrames;
            float y, angle;
            if (t < 0.3f) {
                float lt = t / 0.3f;
                y = -4f * bounceH * (float) Math.pow(lt - 0.5, 2) + bounceH;
                angle = 45f * (1f - 2f * Math.abs(lt - 0.5f));
            } else if (t < 0.6f) {
                float lt = (t - 0.3f) / 0.3f;
                y = -4f * bounceH * (float) Math.pow(lt - 0.5, 2) + bounceH;
                angle = -45f * (1f - 2f * Math.abs(lt - 0.5f));
            } else if (t < 0.65f) {
                y = imgH * 0.2f;
                angle = 0f;
            } else if (t < 0.95f) {
                float lt = (t - 0.65f) / 0.3f;
                y = bounceH * (1 - lt);
                angle = 360f * lt;
            } else {
                y = 0;
                angle = 360f;
            }
            var rotated = ImageEdit.rotate(img, angle);
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.drawImage(rotated, centerX - rotated.getWidth() / 2f, groundY - y - rotated.getHeight() / 2f);
            return surface.makeImageSnapshot();
        }, totalFrames, 0.05f);
    }
}
