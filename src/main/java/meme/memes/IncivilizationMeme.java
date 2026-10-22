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

/** 移植自 memes/incivilization.rs。 */
@Component
public class IncivilizationMeme extends AbstractMeme {

    public IncivilizationMeme() {
        super(new MemeInfo("incivilization",
                new MemeParams(1, 1, 0, 1, List.of("你刚才说的话不是很礼貌！"), List.of()),
                List.of("不文明"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "你刚才说的话不是很礼貌！" : texts.get(0);
        var frame = MemeResources.loadImage("incivilization/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(57, 42, 528, 117),
                text, 20f, 50f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var image = ImageEdit.perspective(
                ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 150, 150),
                0, 20, 154, 0, 164, 153, 22, 180);
        image = ImageEdit.brightness(image, 0.8f);
        canvas.drawImage(image, 137, 151);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
