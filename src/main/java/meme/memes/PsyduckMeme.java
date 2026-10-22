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

/** 移植自 memes/psyduck.rs。 */
@Component
public class PsyduckMeme extends AbstractMeme {

    public PsyduckMeme() {
        super(new MemeInfo("psyduck",
                new MemeParams(0, 0, 2, 2, List.of("来份", "涩图"), List.of()),
                List.of("可达鸭"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 6, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String[] SIDES = { "left", "left", "left", "", "right", "right", "right", "right", "right", "right", "right", "right", "", "left", "left", "left", "left", "left" };
    private static final int[][] PTS = {
            {0, 11, 154, 0, 161, 89, 20, 104},
            {0, 9, 153, 0, 159, 89, 20, 101},
            {0, 7, 148, 0, 156, 89, 21, 97},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {10, 0, 143, 17, 124, 104, 0, 84},
            {13, 0, 143, 27, 125, 113, 0, 83},
            {13, 0, 143, 27, 125, 113, 0, 83},
            {13, 0, 143, 27, 125, 113, 0, 83},
            {13, 0, 143, 27, 125, 113, 0, 83},
            {13, 0, 141, 23, 120, 102, 0, 82},
            {13, 0, 140, 22, 118, 100, 0, 82},
            {9, 0, 128, 16, 109, 89, 0, 80},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 13, 152, 0, 158, 89, 17, 109},
            {0, 13, 152, 0, 158, 89, 17, 109},
            {0, 17, 149, 0, 155, 90, 17, 120},
            {0, 14, 152, 0, 156, 91, 17, 115},
            {0, 12, 154, 0, 158, 90, 17, 109}
    };
    private static final int[][] POS = {
            {18, 42},
            {15, 38},
            {14, 23},
            {0, 0},
            {298, 18},
            {298, 30},
            {298, 26},
            {298, 30},
            {302, 20},
            {300, 24},
            {299, 22},
            {303, 23},
            {0, 0},
            {35, 36},
            {31, 29},
            {45, 33},
            {40, 27},
            {35, 28}
    };

    private io.github.humbleui.skija.Image textImage(String text) {
        var surface = meme.utils.TextRenderer.newSurface(155, 100);
        int padding = 5;
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                surface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(padding, padding, 155 - padding, 100 - padding),
                text, 30f, 80f,
                meme.utils.TextRenderer.params().fontFamilies("FZSJ-QINGCRJ"));
        return surface.makeImageSnapshot();
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var leftImg = textImage(texts.get(0));
        var rightImg = textImage(texts.get(1));
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            var frame = MemeResources.loadImage("psyduck/%02d.jpg".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            String side = SIDES[i];
            int[] p = PTS[i];
            if (side.equals("left")) {
                var persp = ImageEdit.perspective(leftImg, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
                canvas.drawImage(persp, POS[i][0], POS[i][1]);
            } else if (side.equals("right")) {
                var persp = ImageEdit.perspective(rightImg, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
                canvas.drawImage(persp, POS[i][0], POS[i][1]);
            }
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
