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

/** 移植自 memes/alipay.rs。 */
@Component
public class AlipayMeme extends AbstractMeme {

    public AlipayMeme() {
        super(new MemeInfo("alipay",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("message", null, null, null, meme.core.ParserFlags.none()))),
                List.of("支付宝支付"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 10, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 10, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String DEFAULT_MESSAGE = "https://example.com";

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String message = stringOption(options, "message", DEFAULT_MESSAGE);
        String name = images.get(0).name();
        var frame = MemeResources.loadImage("alipay/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var qr = ImageEdit.resizeExact(meme.utils.QrSupport.qrImage(message), 658, 658);
        canvas.drawImage(qr, 211, 606);
        try (var paint = new io.github.humbleui.skija.Paint().setColor(0xFFFFFFFF)) {
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(482, 877, 116, 116, 12), paint);
        }
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(230, 1290, 850, 1380),
                name, 40f, 70f, meme.utils.TextRenderer.params());
        var image = ImageEdit.roundCorner(ImageEdit.resizeFit(images.get(0).image(), 108, 108, ImageEdit.Fit.COVER), 8f);
        canvas.drawImage(image, 486, 881);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
