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

/** 移植自 memes/luoyonghao_say.rs。 */
@Component
public class LuoyonghaoSayMeme extends AbstractMeme {

    public LuoyonghaoSayMeme() {
        super(new MemeInfo("luoyonghao_say",
                new MemeParams(0, 0, 1, 1, List.of("又不是不能用"), List.of()),
                List.of("罗永浩说"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("luoyonghao_say/0.jpg");
        var textSurface = meme.utils.TextRenderer.newSurface(365, 120);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(40, 10, 325, 110),
                text, 10f, 50f, meme.utils.TextRenderer.params());
        var textImage = textSurface.makeImageSnapshot();
        textImage = ImageEdit.perspective(textImage, 52, 10, 391, 0, 364, 110, 0, 120);
        textImage = ImageEdit.gaussianBlur(textImage, 0.8f, 0.8f);
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 48, 246);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
