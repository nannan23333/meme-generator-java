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

/** 移植自 memes/shake_head.rs。 */
@Component
public class ShakeHeadMeme extends AbstractMeme {

    public ShakeHeadMeme() {
        super(new MemeInfo("shake_head",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("晃脑"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img0 = images.get(0).image();
        int imgW = img0.getWidth();
        int imgH = img0.getHeight();
        int paddingW = imgW / 10;
        int paddingH = imgH / 10;
        int dw = Math.max(1, paddingW) / 8;
        int dh = Math.max(1, paddingH) / 8;
        int frameW = imgW - paddingW * 2;
        int frameH = imgH - paddingH * 2;
        int frameNum = 20;
        double dt = 2.0 * Math.PI / frameNum;
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var img = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            int x = (int) Math.round(paddingW * -Math.sin(-outIdx * dt) - paddingW
                    + (Math.random() * 2 - 1) * dw);
            int y = (int) Math.round(paddingH * -Math.cos(-outIdx * dt) - paddingH
                    + (Math.random() * 2 - 1) * dh);
            canvas.drawImage(img, x, y);
            return surface.makeImageSnapshot();
        }, frameNum, 0.02f);
    }
}
