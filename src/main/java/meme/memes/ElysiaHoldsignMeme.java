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

/** 移植自 memes/elysia_holdsign.rs。 */
@Component
public class ElysiaHoldsignMeme extends AbstractMeme {

    public ElysiaHoldsignMeme() {
        super(new MemeInfo("elysia_holdsign",
                new MemeParams(0, 0, 1 , 1, List.of("要好好爱莉哦~"), List.of(new MemeOption.IntegerOption("number", null, 1, 8, null, meme.core.ParserFlags.none()))),
                List.of("爱莉举牌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 8, 9, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 8, 9, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String DEFAULT_TEXT = "要好好爱莉哦~";
    // 每项：{w, h, px, py, x0, y0, x1, y1, x2, y2, x3, y3}
    private static final int[][] PARAMS = {
            {300, 200, 144, 322, 0, 66, 276, 0, 319, 178, 43, 244},
            {300, 250, -46, -50, 0, 83, 312, 0, 348, 243, 46, 314},
            {300, 150, 106, 351, 0, 0, 286, 0, 276, 149, 12, 149},
            {250, 200, 245, -6, 31, 0, 288, 49, 256, 239, 0, 190},
            {500, 200, 0, 0, 0, 0, 492, 0, 462, 198, 25, 198},
            {350, 150, 74, 359, 0, 52, 345, 0, 364, 143, 31, 193},
            {270, 200, 231, -9, 31, 0, 305, 49, 270, 245, 0, 192},
            {350, 150, 64, 340, 0, 44, 345, 0, 358, 153, 34, 197}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? DEFAULT_TEXT : texts.get(0);
        int num = 1 + (int) (Math.random() * 8);
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = n.intValue();
        }
        if (num < 1 || num > 8) {
            throw meme.core.MemeError.feedback("图片编号错误，请输入 1-8");
        }
        int[] p = PARAMS[num - 1];
        var frame = MemeResources.loadImage("elysia_holdsign/%02d.png".formatted(num));
        var textSurface = meme.utils.TextRenderer.newSurface(p[0], p[1]);
        int padding = 10;
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(padding, padding, p[0] - padding, p[1] - padding),
                text, 30f, 80f,
                meme.utils.TextRenderer.params()
                        .align(meme.utils.TextRenderer.Params.Align.CENTER)
                        .fontFamilies("FZShaoEr-M11S").fill(0xFF3B0B07));
        var textImage = textSurface.makeImageSnapshot();
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var persp = ImageEdit.perspective(textImage, p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11]);
        canvas.drawImage(persp, p[2], p[3]);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
