package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** coupon 兑换券：移植自 memes/coupon.rs。 */
@Component
public class CouponMeme extends AbstractMeme {

    public CouponMeme() {
        super(new MemeInfo("coupon",
                new MemeParams(1, 1, 0, 1, List.of(), List.of()),
                List.of("兑换券"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty()
                ? images.get(0).name() + "陪睡券\n（永久有效）"
                : texts.get(0);

        // 文字贴图 250x100，15-30px
        var textSurface = meme.utils.TextRenderer.newSurface(250, 100);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeXYWH(0, 0, 250, 100),
                text, 15f, 30f, meme.utils.TextRenderer.params());
        var textImage = textSurface.makeImageSnapshot();

        var frame = MemeResources.loadImage("coupon/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var image = ImageEdit.rotate(
                ImageEdit.resizeFit(ImageEdit.circle(images.get(0).image()), 60, 60, ImageEdit.Fit.COVER), -22f);
        var rotatedText = ImageEdit.rotate(textImage, -22f);
        canvas.drawImage(image, 164, 85);
        canvas.drawImage(rotatedText, 94, 108);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
