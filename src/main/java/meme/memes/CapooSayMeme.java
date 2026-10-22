package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.TextRenderer;
import io.github.humbleui.skija.Image;
import io.github.humbleui.types.Rect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * capoo_say 咖波说：文字 GIF，1–10 段文字。
 * 移植自 memes/capoo_say.rs。
 */
@Component
public class CapooSayMeme extends AbstractMeme {

    // (w, h, x, y, angle) —— 第 3~8 帧文字贴图的变换参数
    private static final int[][] PARAMS = {
            {45, 45, 74, 112, -25},
            {73, 73, 41, 42, -17},
            {80, 80, 43, 36, 0},
            {80, 80, 43, 30, 0},
            {78, 78, 44, 30, 0},
            {78, 78, 44, 29, 0},
    };

    private static final java.time.ZoneOffset ZONE = java.time.ZoneOffset.ofHours(8);

    public CapooSayMeme() {
        super(new MemeInfo(
                "capoo_say",
                new MemeParams(0, 0, 1, 10, List.of("寄"), List.of()),
                List.of("咖波说"),
                List.of(),
                Set.of("咖波"),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZONE),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 30, 0, 0), ZONE)));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        List<Image> frames = new ArrayList<>();
        for (String text : texts) {
            // 文字贴图：80x80 区域内自动字号（20~80）
            var textSurface = TextRenderer.newSurface(80, 80);
            TextRenderer.drawTextAreaAutoFontSize(
                    textSurface.getCanvas(), Rect.makeXYWH(0, 0, 80, 80), text,
                    20f, 80f, TextRenderer.params().fontFamilies("FZKaTong-M19S"));
            Image textImage = textSurface.makeImageSnapshot();

            for (int i = 0; i < 10; i++) {
                var frame = MemeResources.loadImage("capoo_say/%d.png".formatted(i));
                var surface = TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
                var canvas = surface.getCanvas();
                canvas.drawImage(frame, 0, 0);
                if (i >= 3 && i < 9) {
                    int[] p = PARAMS[i - 3];
                    Image scaled = ImageEdit.resizeExact(textImage, p[0], p[1]);
                    Image rotated = ImageEdit.rotate(scaled, p[4]);
                    canvas.drawImage(rotated, p[2], p[3]);
                }
                frames.add(surface.makeImageSnapshot());
            }
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
