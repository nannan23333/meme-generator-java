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

/** 移植自 memes/flick.rs。 */
@Component
public class FlickMeme extends AbstractMeme {

    public FlickMeme() {
        super(new MemeInfo("flick",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("弹", "脑瓜崩"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 6, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 6, 22, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeExact(ImageEdit.square(input), 240, 240);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (i < 3) {
                frames.add(img);
            } else if (i < 12) {
                var surface = meme.utils.TextRenderer.newSurface(240, 240);
                var canvas = surface.getCanvas();
                canvas.drawImage(img, 0, 0);
                var hand = MemeResources.loadImage("flick/%d.png".formatted(i - 3));
                canvas.drawImage(hand, 0, 0);
                frames.add(surface.makeImageSnapshot());
            } else {
                float width = 240f;
                float amplitude = width / 2f;
                double omega = 2.0 * Math.PI / 5.0;
                float decay = 0.1f;
                float maxAngle = 0.5f;
                float t = i - 3;
                double damping = Math.exp(-decay * t);
                float offset = (float) (amplitude * Math.sin(omega * t) * damping);
                float angle = (float) (maxAngle * Math.sin(omega * t) * damping);
                // 对齐 Rust SkSL：逐行 factor 旋转 + 平移
                var mapped = ImageEdit.remap(img, (x, y) -> {
                    float factor = 1f - y / 240f;
                    float dx = factor * offset;
                    float a = angle * factor;
                    float cx = 120f, cy = 120f;
                    float rx = x - cx, ry = y - cy;
                    float ca = (float) Math.cos(a);
                    float sa = (float) Math.sin(a);
                    return new float[]{
                            rx * ca - ry * sa + cx + dx,
                            rx * sa + ry * ca + cy};
                });
                frames.add(mapped);
            }
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
