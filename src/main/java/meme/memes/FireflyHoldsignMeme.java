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

/** 移植自 memes/firefly_holdsign.rs。 */
@Component
public class FireflyHoldsignMeme extends AbstractMeme {

    public FireflyHoldsignMeme() {
        super(new MemeInfo("firefly_holdsign",
                new MemeParams(0, 0, 1, 1, List.of("我超爱你"), List.of()),
                List.of("流萤举牌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 5, 6, 0, 0), ZoneOffset.ofHours(8))));
    }

    // 每项：{w, h, px, py, x0, y0, x1, y1, x2, y2, x3, y3}
    private static final int[][] PARAMS = {
            {300, 200, 144, 322, 0, 66, 276, 0, 319, 178, 43, 244},
            {300, 250, -46, -50, 0, 83, 312, 0, 348, 243, 46, 314},
            {300, 150, 106, 351, 0, 0, 286, 0, 276, 149, 12, 149},
            {250, 200, 245, -6, 31, 0, 288, 49, 256, 239, 0, 190},
            {500, 200, 0, 0, 0, 0, 492, 0, 462, 198, 25, 198},
            {350, 150, 74, 359, 0, 52, 345, 0, 364, 143, 31, 193},
            {270, 200, 231, -9, 31, 0, 305, 49, 270, 245, 0, 192},
            {350, 150, 64, 340, 0, 44, 345, 0, 358, 153, 34, 197},
            {230, 100, 57, 261, 10, 0, 243, 38, 222, 132, 0, 99},
            {240, 150, -24, -20, 0, 32, 235, 0, 254, 146, 24, 182},
            {230, 140, 133, -35, 40, 0, 267, 68, 227, 203, 0, 133},
            {169, 124, 107, 236, 0, 0, 169, 0, 169, 124, 0, 124},
            {210, 140, 156, -7, 24, 0, 227, 32, 204, 172, 0, 136},
            {250, 123, 53, 237, 0, 3, 250, 0, 250, 123, 0, 123},
            {200, 140, 168, -9, 29, 0, 222, 40, 192, 177, 0, 135},
            {256, 96, 50, 264, 0, 0, 256, 0, 256, 96, 0, 96},
            {120, 200, 174, 130, 116, 0, 240, 67, 117, 269, 0, 195},
            {250, 140, -42, -27, 0, 77, 244, 0, 288, 132, 42, 210},
            {230, 130, -64, -42, 0, 110, 229, 0, 294, 126, 64, 245},
            {183, 133, 0, 227, 0, 0, 183, 9, 183, 133, 0, 133},
            {255, 106, 50, 254, 2, 4, 256, 0, 257, 106, 0, 106}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        int num = 1 + (int) (Math.random() * 21);
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = Math.max(1, Math.min(21, n.intValue()));
        }
        int[] p = PARAMS[num - 1];
        var frame = MemeResources.loadImage("firefly_holdsign/%02d.png".formatted(num));
        var textSurface = meme.utils.TextRenderer.newSurface(p[0], p[1]);
        int padding = 10;
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(padding, padding, p[0] - padding, p[1] - padding),
                text, 30f, 80f,
                meme.utils.TextRenderer.params()
                        .align(meme.utils.TextRenderer.Params.Align.CENTER)
                        .fontFamilies("FZShaoEr-M11S"));
        var textImage = textSurface.makeImageSnapshot();
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var persp = ImageEdit.perspective(textImage, p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11]);
        canvas.drawImage(persp, p[2], p[3]);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
