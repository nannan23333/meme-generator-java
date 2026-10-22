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

/** 移植自 memes/blamed_mahiro.rs。 */
@Component
public class BlamedMahiroMeme extends AbstractMeme {

    public BlamedMahiroMeme() {
        super(new MemeInfo("blamed_mahiro",
                new MemeParams(0, 0, 1, 1, List.of("傻逼"), List.of()),
                List.of("真寻挨骂"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 26, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 26, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String DEFAULT_TEXT = "傻逼";
    private static final int[][] PARAMS = {
            {70, 0, 50, 84, 50, 100, 0, 1, 98, 94},
            {70, 0, 50, 84, 50, 100, 0, 1, 98, 94},
            {55, 0, 55, 128, 45, 208, 0, 32, 183, 16},
            {58, 0, 98, 148, 76, 162, 0, 34, 254, -4},
            {68, 0, 114, 164, 95, 172, 0, 46, 244, 4},
            {64, 0, 126, 156, 98, 172, 0, 54, 240, 20},
            {71, 0, 159, 185, 129, 203, 0, 40, 202, 70},
            {71, 0, 159, 185, 129, 203, 0, 40, 202, 70},
            {67, 0, 143, 187, 111, 201, 0, 33, 198, 91},
            {66, 0, 131, 195, 98, 211, 0, 33, 186, 83},
            {66, 0, 131, 195, 98, 211, 0, 33, 186, 83},
            {65, 0, 108, 195, 81, 204, 0, 28, 166, 37},
            {66, 0, 96, 190, 68, 197, 0, 28, 164, 8},
            {66, 0, 96, 190, 68, 197, 0, 28, 164, 8},
            {70, 0, 91, 186, 65, 197, 0, 26, 162, -6},
            {70, 0, 91, 187, 65, 197, 0, 27, 158, -20},
            {70, 0, 91, 187, 65, 197, 0, 27, 158, -20},
            {74, 0, 82, 190, 58, 197, 0, 30, 174, -13},
            {74, 0, 84, 192, 59, 200, 0, 35, 182, -12},
            {74, 0, 84, 192, 59, 200, 0, 35, 182, -12},
            {78, 0, 86, 196, 62, 205, 0, 35, 182, -10},
            {76, 0, 84, 194, 58, 205, 0, 36, 188, -9}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? DEFAULT_TEXT : texts.get(0);
        var textSurface = meme.utils.TextRenderer.newSurface(400, 80);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(0, -5, 350, 85),
                text, 60f, 80f,
                meme.utils.TextRenderer.params().fontFamilies("FZKaTong-M19S")
                        .align(meme.utils.TextRenderer.Params.Align.LEFT)
                        .fill(0xFF66334D).strokePx(1.5f, 0xFFFFFFFF));
        var textImage = textSurface.makeImageSnapshot();
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            var frame = MemeResources.loadImage("blamed_mahiro/%02d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            if (i >= 2) {
                int[] p = PARAMS[i - 2];
                var persp = ImageEdit.perspective(textImage, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
                canvas.drawImage(persp, p[8], p[9]);
            }
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
