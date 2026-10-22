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

/** 移植自 memes/left_right_jump.rs。 */
@Component
public class LeftRightJumpMeme extends AbstractMeme {

    public LeftRightJumpMeme() {
        super(new MemeInfo("left_right_jump",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("direction", "left_right", List.of("left_right", "right_left"), null, meme.core.ParserFlags.none()))),
                List.of("左右横跳"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String dir = stringOption(options, "direction", "left_right");
        if (boolOption(options, "left_right", false)) dir = "left_right";
        if (boolOption(options, "right_left", false)) dir = "right_left";
        final String direction = dir;
        boolean reverse = direction.equals("right_left");
        int imgW = 100;
        int frameW = 300;
        int frameNum = 30;
        float dx = (frameW - imgW) / (frameNum / 2f - 1);
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int imgH = input.getHeight() * imgW / input.getWidth();
            var img = ImageEdit.resizeWidth(input, imgW);
            int frameH = imgH + 30;
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.clear(0x00000000);
            // 对齐 Rust：left_right 从右侧起步向左、后半程折返并水平翻转；
            // right_left 从左侧起步、全程不翻转
            float x;
            boolean flip;
            if (!reverse) {
                if (outIdx >= frameNum / 2) {
                    x = frameW - imgW - dx * (frameNum - 1 - outIdx);
                    flip = true;
                } else {
                    x = frameW - imgW - dx * outIdx;
                    flip = false;
                }
            } else {
                x = outIdx >= frameNum / 2
                        ? dx * (frameNum - 1 - outIdx)
                        : dx * outIdx;
                flip = false;
            }
            if (flip) {
                img = ImageEdit.flipHorizontal(img);
            }
            float midX = x + imgW / 2f;
            float h = 15f;
            float w = (frameW - imgW) / 4f;
            float k = h / (w * w);
            float y;
            if (midX < imgW / 2f || midX > frameW - imgW / 2f) {
                y = 0;
            } else if (midX < frameW / 2f) {
                y = h - k * (float) Math.pow(midX - imgW / 2f - w, 2);
            } else {
                y = h - k * (float) Math.pow(midX - imgW / 2f - w * 3, 2);
            }
            canvas.drawImage(img, Math.round(x), Math.round(frameH - imgH - y));
            return surface.makeImageSnapshot();
        }, frameNum, 0.04f);
    }
}
