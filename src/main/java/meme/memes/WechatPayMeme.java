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

/** 移植自 memes/wechat_pay.rs。 */
@Component
public class WechatPayMeme extends AbstractMeme {

    public WechatPayMeme() {
        super(new MemeInfo("wechat_pay",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("message", null, null, null, meme.core.ParserFlags.none()))),
                List.of("微信支付"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 10, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 10, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String DEFAULT_MESSAGE = "https://example.com";

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String message = stringOption(options, "message", DEFAULT_MESSAGE);
        String name = images.get(0).name();
        var bg = MemeResources.loadImage("wechat_pay/0.png");
        var surface = meme.utils.TextRenderer.newSurface(bg.getWidth(), bg.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(bg, 0, 0);
        var qr = ImageEdit.resizeExact(meme.utils.QrSupport.qrImage(message), 530, 530);
        canvas.drawImage(qr, 356, 439);
        try (var paint = new io.github.humbleui.skija.Paint().setColor(0xFFFFFFFF)) {
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(528, 611, 186, 186), paint);
        }
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(370, 1000, 872, 1100),
                name, 50f, 80f, meme.utils.TextRenderer.params());
        var logo = MemeResources.loadImage("wechat_pay/logo.png");
        var image = ImageEdit.roundCorner(ImageEdit.resizeFit(images.get(0).image(), 166, 166, ImageEdit.Fit.COVER), 8f);
        canvas.drawImage(image, 538, 621);
        canvas.drawImage(logo, 649, 734);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
