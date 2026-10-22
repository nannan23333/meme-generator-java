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

/** 移植自 memes/funny_mirror.rs。 */
@Component
public class FunnyMirrorMeme extends AbstractMeme {

    public FunnyMirrorMeme() {
        super(new MemeInfo("funny_mirror",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("哈哈镜"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            float strength = 0.5f * (float) Math.sin((float) (Math.PI / 20.0 * outIdx));
            float x = input.getWidth() * 0.5f;
            float y = input.getHeight() * 0.5f;
            float radius = (float) Math.sqrt(x * x + y * y);
            // 对齐 Rust SkSL：offset *= mix(1, smoothstep(0, radius/d, d/radius), strength)
            return ImageEdit.remap(input, (px, py) -> {
                float ox = px - x;
                float oy = py - y;
                float d = (float) Math.sqrt(ox * ox + oy * oy);
                // SkSL: smoothstep(0.0, radius/d, d/radius) → x/edge1 = (d/r)/(r/d) = (d/r)^2
                float t = Math.max(0f, Math.min(1f, (d * d) / (radius * radius)));
                float smooth = t * t * (3 - 2 * t);
                float factor = 1f + (smooth - 1f) * strength;
                return new float[]{x + ox * factor, y + oy * factor};
            });
        }, 21, 0.05f);
    }
}
