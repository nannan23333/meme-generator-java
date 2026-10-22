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

/** 移植自 memes/my_wife.rs。 */
@Component
public class MyWifeMeme extends AbstractMeme {

    public MyWifeMeme() {
        super(new MemeInfo("my_wife",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("pronoun", "我", null, null, meme.core.ParserFlags.none()), new MemeOption.StringOption("name", "老婆", null, null, meme.core.ParserFlags.none()))),
                List.of("我老婆", "这是我老婆"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 25, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String pronoun = stringOption(options, "pronoun", "你");
        String name = stringOption(options, "name", "老婆");
        var img0 = images.get(0).image();
        int imgW = 400;
        int imgH = img0.getHeight() * imgW / img0.getWidth();
        var surface = meme.utils.TextRenderer.newSurface(650, imgH + 500);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        String[] lines = {
                "如果你的" + name + "长这样",
                "那么这就不是你的" + name + "\n这是" + pronoun + "的" + name,
                "滚去找你\n自己的" + name + "去"};
        int[][] rects = {
                {27, 12, 27 + 596, 12 + 79},
                {27, imgH + 120, 27 + 593, imgH + 120 + 135},
                {27, imgH + 295, 27 + 374, imgH + 295 + 135}};
        for (int i = 0; i < 3; i++) {
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeLTRB(rects[i][0], rects[i][1], rects[i][2], rects[i][3]),
                    lines[i], 30f, 70f,
                    meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        }
        var point = ImageEdit.resizeWidth(MemeResources.loadImage("my_wife/0.png"), 200);
        canvas.drawImage(point, 421, imgH + 270);
        var composed = surface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var s2 = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var c2 = s2.getCanvas();
            c2.drawImage(composed, 0, 0);
            var img = ImageEdit.resizeWidth(input, imgW);
            c2.drawImage(img, 325 - imgW / 2, 105);
            return s2.makeImageSnapshot();
        });
    }
}
