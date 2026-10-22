package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/caused_by_this.rs。 */
@Component
public class CausedByThisMeme extends AbstractMeme {

    public CausedByThisMeme() {
        super(new MemeInfo("caused_by_this",
                new MemeParams(1, 1, 0, 1, List.of("心脏病 高血压 心律不齐 心肌梗塞 失眠 脱发 呼吸困难 胸闷气短 缺氧 躁郁 焦虑 脑供血不足 心慌心悸 心脑血管炸裂"), List.of()),
                List.of("这个引起的"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 18, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 22, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String DEFAULT_TEXT = "心脏病 高血压 心律不齐 心肌梗塞 失眠 脱发 呼吸困难 胸闷气短 缺氧 躁郁 焦虑 脑供血不足 心慌心悸 心脑血管炸裂";

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? DEFAULT_TEXT : texts.get(0);
        var frame = MemeResources.loadImage("caused_by_this/0.png");
        var textImg1 = Text2Image.fromText("你的", 55f, null, null, null, null);
        var textImg2 = Text2Image.fromText("主要都是由这个引起的", 55f, null, null, null, null);
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        textImg1.drawOnCanvas(canvas, 10, 887 - textImg1.height() / 2f);
        textImg2.drawOnCanvas(canvas, frame.getWidth() - textImg2.longestLine() - 10f,
                887 - textImg2.height() / 2f);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(
                        Math.round(textImg1.longestLine()) + 20, 760,
                        frame.getWidth() - Math.round(textImg2.longestLine()) - 20, 1000),
                text, 10f, 60f, meme.utils.TextRenderer.params());
        var composed = surface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var s2 = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var c2 = s2.getCanvas();
            c2.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, 550, 360, ImageEdit.Fit.COVER);
            c2.drawImage(image, 122, 9);
            c2.drawImage(composed, 0, 0);
            return s2.makeImageSnapshot();
        });
    }
}
