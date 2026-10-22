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

/** 移植自 memes/remote_control.rs。 */
@Component
public class RemoteControlMeme extends AbstractMeme {

    public RemoteControlMeme() {
        super(new MemeInfo("remote_control",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("遥控", "控制"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int imgW = Math.min(input.getWidth(), 500);
            var img = ImageEdit.resizeWidth(input, imgW);
            imgW = img.getWidth();
            int imgH = img.getHeight();
            var surface = meme.utils.TextRenderer.newSurface(imgW, imgH);
            var canvas = surface.getCanvas();
            int dx = 0, dy = 0;
            if (outIdx >= 4) {
                dx = (int) (imgW * (Math.random() * 2 - 1) / 60.0);
                dy = (int) (imgH * (Math.random() * 2 - 1) / 60.0);
            }
            canvas.drawImage(img, dx, dy);
            var overlay = ImageEdit.resizeHeight(
                    MemeResources.loadImage("remote_control/%02d.png".formatted(outIdx)),
                    (int) (imgH / 1.5f));
            canvas.drawImage(overlay, imgW - overlay.getWidth(), imgH - overlay.getHeight());
            return surface.makeImageSnapshot();
        }, 17, 0.07f);
    }
}
