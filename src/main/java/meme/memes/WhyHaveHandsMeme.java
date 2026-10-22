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

/** 移植自 memes/why_have_hands.rs。 */
@Component
public class WhyHaveHandsMeme extends AbstractMeme {

    public WhyHaveHandsMeme() {
        super(new MemeInfo("why_have_hands",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("为什么要有手"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 18, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        var img0 = images.get(0).image();

        var textSurface = meme.utils.TextRenderer.newSurface(600, 100);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(0, 0, 600, 100),
                "摸摸" + name + "!", 30f, 70f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf")
                        .align(meme.utils.TextRenderer.Params.Align.LEFT));
        var textImage = textSurface.makeImageSnapshot();

        var frame = MemeResources.loadImage("why_have_hands/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        canvas.drawImage(ImageEdit.rotateCrop(ImageEdit.resizeExact(ImageEdit.square(img0), 250, 250), -15f), 1001, 668);
        canvas.drawImage(ImageEdit.resizeFit(img0, 250, 170, ImageEdit.Fit.COVER), 275, 1100);
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(ImageEdit.resizeExact(ImageEdit.circle(img0), 250, 250), 350, 670);
        canvas.drawImage(ImageEdit.resizeFit(img0, 300, 400, ImageEdit.Fit.CONTAIN), 1100, 1060);
        canvas.drawImage(ImageEdit.rotate(textImage, 15f), 75, 825);
        var p = meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf");
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(840, 960, 1440, 1060),
                "托托" + name + "!", 30f, 70f, p);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(50, 1325, 650, 1475),
                "赞美" + name + "!", 30f, 90f, p);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(700, 1340, 1075, 1490),
                "为" + name + "奉献所有财产!", 30f, 70f, p);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
