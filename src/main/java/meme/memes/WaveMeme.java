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

/** 移植自 memes/wave.rs。 */
@Component
public class WaveMeme extends AbstractMeme {

    public WaveMeme() {
        super(new MemeInfo("wave",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("波纹"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 26, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img0 = images.get(0).image();
        int imgW = Math.max(360, Math.min(720, img0.getWidth()));
        float period = imgW / 6f;
        float amp = imgW / 60f;
        int frameNum = 8;
        final float omega = (float) (2.0 * Math.PI / period);
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var img = ImageEdit.resizeWidth(input, imgW);
            int w = img.getWidth();
            int h = img.getHeight();
            float a = amp / 2f;
            float phi = outIdx * period / frameNum;
            // 对齐 Rust SkSL：dx = a*sin(omega*(x+phi))*(h-y)/h; dy = a*sin(omega*(y+phi))*y/h
            var mapped = ImageEdit.remap(img, (x, y) -> {
                float dx = a * (float) Math.sin(omega * (x + phi)) * (h - y) / h;
                float dy = a * (float) Math.sin(omega * (y + phi)) * y / h;
                return new float[]{x + dx, y + dy};
            });
            return mapped;
        }, frameNum, 0.01f);
    }
}
