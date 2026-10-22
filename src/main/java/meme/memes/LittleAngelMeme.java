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

/** 移植自 memes/little_angel.rs。 */
@Component
public class LittleAngelMeme extends AbstractMeme {

    public LittleAngelMeme() {
        super(new MemeInfo("little_angel",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("gender", "unknown", List.of("male", "female", "unknown"), null, meme.core.ParserFlags.none()))),
                List.of("小天使"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 4, 20, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String gender = stringOption(options, "gender", "female");
        String ta = gender.equals("male") ? "他" : "她";
        String name = images.get(0).name();
        if (name == null || name.isEmpty()) {
            name = ta;
        }
        var img0 = images.get(0).image();
        int imgW = 500;
        int imgH = img0.getHeight() * imgW / img0.getWidth();
        var surface = meme.utils.TextRenderer.newSurface(600, imgH + 230);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(20, 20, 580, 110),
                "请问你们看到" + name + "了吗?", 40f, 70f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        meme.utils.TextRenderer.drawTextArea(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(10, imgH + 115, 590, imgH + 190),
                "非常可爱！简直就是小天使", 48f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        meme.utils.TextRenderer.drawTextArea(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(20, imgH + 175, 580, imgH + 220),
                ta + "没失踪也没怎么样  我只是觉得你们都该看一下", 26f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var composed = surface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var s2 = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var c2 = s2.getCanvas();
            c2.drawImage(composed, 0, 0);
            var img = ImageEdit.resizeWidth(input, imgW);
            c2.drawImage(img, 50, 110);
            return s2.makeImageSnapshot();
        });
    }
}
