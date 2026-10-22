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

/** 移植自 memes/shock.rs。 */
@Component
public class ShockMeme extends AbstractMeme {

    public ShockMeme() {
        super(new MemeInfo("shock",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("震惊"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeExact(ImageEdit.square(input), 300, 300);
        // 每帧随机方向 + 强度 + 旋转（对齐 Rust 逻辑）
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int f = 0; f < 20; f++) {
            float angle = (float) Math.toRadians(-90 + (int) (Math.random() * 181));
            float dx = (float) Math.cos(angle);
            float dy = (float) Math.sin(angle);
            int steps = (int) (Math.random() * 51);
            float rotate = -20 + (float) (Math.random() * 41);
            io.github.humbleui.skija.Image frame;
            if (steps == 0) {
                frame = img;
            } else {
                var src = ImageEdit.toBufferedImage(img);
                int w = src.getWidth();
                int h = src.getHeight();
                var dst = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        float rr = 0, gg = 0, bb = 0, aa = 0;
                        float totalW = 0;
                        for (int i = -50; i <= 50; i++) {
                            if (Math.abs(i) > steps) continue;
                            float weight = 1f - (float) Math.abs(i) / steps;
                            int sx = Math.round(x + dx * i);
                            int sy = Math.round(y + dy * i);
                            if (sx >= 0 && sx < w && sy >= 0 && sy < h) {
                                int rgb = src.getRGB(sx, sy);
                                rr += ((rgb >> 16) & 0xFF) * weight;
                                gg += ((rgb >> 8) & 0xFF) * weight;
                                bb += (rgb & 0xFF) * weight;
                                aa += ((rgb >> 24) & 0xFF) * weight;
                                totalW += weight;
                            }
                        }
                        if (totalW > 0) {
                            dst.setRGB(x, y, ((int) (aa / totalW) << 24)
                                    | ((int) (rr / totalW) << 16)
                                    | ((int) (gg / totalW) << 8)
                                    | (int) (bb / totalW));
                        }
                    }
                }
                frame = ImageEdit.fromBufferedImage(dst);
            }
            var rotated = ImageEdit.rotate(frame, rotate);
            var surface = meme.utils.TextRenderer.newSurface(rotated.getWidth(), rotated.getHeight());
            surface.getCanvas().drawImage(rotated, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.01f);
    }
}
