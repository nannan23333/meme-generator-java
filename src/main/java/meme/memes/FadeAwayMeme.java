package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/fade_away.rs。 */
@Component
public class FadeAwayMeme extends AbstractMeme {

    public FadeAwayMeme() {
        super(new MemeInfo("fade_away",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("灰飞烟灭"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    /** 尘埃粒子，对齐 Rust Dot。 */
    private static final class Dot {
        float x, y, vx, vy, dx, dy, radius;
        boolean out;

        Dot(float px, float py, float dx, float dy) {
            this.x = px;
            this.y = py;
            this.dx = dx;
            this.dy = dy;
            this.radius = 1f + (float) (Math.random() * 3f);
        }

        void moveStep(float step) {
            float a = 0.02f * step / radius;
            vx += a * dx;
            vy += a * dy;
            x += vx;
            y += vy;
            if (Math.random() < 0.25) {
                radius -= 1f;
            }
        }

        void drawOn(io.github.humbleui.skija.Canvas canvas) {
            try (var paint = new io.github.humbleui.skija.Paint().setColor(0xFF000000)) {
                canvas.drawCircle(x, y, radius, paint);
            }
        }
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        List<Dot> dusts = new ArrayList<>();
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var img = input.getWidth() > 200 ? ImageEdit.resizeWidth(input, 200) : input;
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            float centerX = imgW * 2f / 3f;
            float centerY = imgH * 3f / 2f;
            float radius = (float) Math.sqrt(centerX * centerX + centerY * centerY);
            float step = radius / 24f;

            if (outIdx <= 9) {
                return img;
            }
            var surface = meme.utils.TextRenderer.newSurface(imgW, imgH);
            var canvas = surface.getCanvas();
            if (outIdx < 28) {
                int t = outIdx - 9;
                float r1 = step * (t + 4);
                float r2 = step * (t + 5);
                float r3 = step * (t + 11);
                // 对齐 Rust SkSL：距离场三段处理
                var mapped = ImageEdit.remap(img, (x, y) -> x >= 0 && y >= 0 ? new float[]{x, y} : new float[]{x, y});
                var bi = ImageEdit.toBufferedImage(img);
                var dst = new java.awt.image.BufferedImage(imgW, imgH, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < imgH; y++) {
                    for (int x = 0; x < imgW; x++) {
                        int argb = bi.getRGB(x, y);
                        int alpha = (argb >>> 24);
                        if (alpha == 0) {
                            continue;
                        }
                        float d = (float) Math.hypot(x - centerX, y - centerY);
                        if (d <= r1) {
                            continue; // 透明
                        }
                        if (d <= r2) {
                            dst.setRGB(x, y, 0xFF000000);
                            continue;
                        }
                        if (d <= r3) {
                            float factor = 0.5f + (d - r2) / (r3 - r2);
                            factor = Math.max(0f, Math.min(1f, factor));
                            double rnd = frac(Math.sin(dot(x, y)) * 43758.5453);
                            factor *= 0.9f + 0.2f * (float) rnd;
                            int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
                            int gray = Math.round((r + g + b) / 3f * factor);
                            dst.setRGB(x, y, (alpha << 24) | (gray << 16) | (gray << 8) | gray);
                            continue;
                        }
                        dst.setRGB(x, y, argb);
                    }
                }
                var frame = ImageEdit.fromBufferedImage(dst);
                canvas.drawImage(frame, 0, 0);
                // 生成尘埃（r1..r2 环形采样）
                var src = ImageEdit.toBufferedImage(img);
                for (int r = (int) r1; r < r2; r++) {
                    for (int theta = 0; theta < 180; theta++) {
                        double rad = Math.toRadians(theta);
                        int x = (int) (centerX + r * Math.cos(rad));
                        int y = (int) (centerY - r * Math.sin(rad));
                        if (x < 0 || x >= imgW || y < 0 || y >= imgH) continue;
                        int c = src.getRGB(x, y);
                        if ((c >>> 24) == 0) continue;
                        if (Math.random() < 0.1) {
                            float dx = (x - centerX) / radius;
                            float dy = 1.5f * (y - centerY) / radius;
                            dusts.add(new Dot(x, y, dx, dy));
                        }
                    }
                }
            } else {
                canvas.clear(0x00000000);
            }
            for (Dot dot : dusts) {
                if (dot.out) continue;
                dot.moveStep(step);
                if (dot.radius <= 0 || dot.x + dot.radius < 0 || dot.x - dot.radius > imgW
                        || dot.y + dot.radius < 0 || dot.y - dot.radius > imgH) {
                    dot.out = true;
                } else {
                    dot.drawOn(canvas);
                }
            }
            return surface.makeImageSnapshot();
        }, 35, 0.08f);
    }

    private static double dot(int x, int y) {
        return x * 12.9898 + y * 78.233;
    }

    private static double frac(double v) {
        return v - Math.floor(v);
    }
}
