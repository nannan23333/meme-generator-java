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

/** 移植自 memes/loop_.rs。 */
@Component
public class LoopMeme extends AbstractMeme {

    public LoopMeme() {
        super(new MemeInfo("loop",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("direction", "top", List.of("left", "right", "top", "bottom"), null, meme.core.ParserFlags.none()))),
                List.of("循环"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String dir = stringOption(options, "direction", "top");
        if (boolOption(options, "left", false)) dir = "left";
        if (boolOption(options, "right", false)) dir = "right";
        if (boolOption(options, "top", false)) dir = "top";
        if (boolOption(options, "bottom", false)) dir = "bottom";
        final String direction = dir;
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int imgW = input.getWidth();
            int imgH = input.getHeight();
            var surface = meme.utils.TextRenderer.newSurface(imgW, imgH);
            var canvas = surface.getCanvas();
            if (direction.equals("top") || direction.equals("bottom")) {
                int h = (int) (imgH / 30.0 * outIdx);
                if (direction.equals("top")) {
                    canvas.drawImage(input, 0, -h);
                    canvas.drawImage(input, 0, imgH - h);
                } else {
                    canvas.drawImage(input, 0, h);
                    canvas.drawImage(input, 0, h - imgH);
                }
            } else {
                int w = (int) (imgW / 30.0 * outIdx);
                if (direction.equals("left")) {
                    canvas.drawImage(input, -w, 0);
                    canvas.drawImage(input, imgW - w, 0);
                } else {
                    canvas.drawImage(input, w, 0);
                    canvas.drawImage(input, w - imgW, 0);
                }
            }
            return surface.makeImageSnapshot();
        }, 30, 0.05f);
    }
}
