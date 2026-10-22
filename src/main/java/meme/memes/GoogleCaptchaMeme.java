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

/** 移植自 memes/google_captcha.rs。 */
@Component
public class GoogleCaptchaMeme extends AbstractMeme {

    public GoogleCaptchaMeme() {
        super(new MemeInfo("google_captcha",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("谷歌验证码"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 15, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        var surface = meme.utils.TextRenderer.newSurface(1004, 1539);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFD5D5D5);
        try (var white = new io.github.humbleui.skija.Paint().setColor(0xFFFFFFFF)) {
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(2, 2, 1000, 1535), white);
        }
        var tp = meme.utils.TextRenderer.params().fill(0xFFFFFFFF)
                .fontFamilies("NotoSansSC-Bold.ttf");
        try (var blue = new io.github.humbleui.skija.Paint().setColor(0xFF4790E4)) {
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(21, 21, 962, 332), blue);
        }
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(91, 81, 921, 141),
                "请选择包含", 40f, tp);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(91, 141, 921, 231),
                name, 20f, 80f, tp);
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(91, 231, 921, 291),
                "的所有图块，如果没有，请点击“跳过”", 40f, tp);
        try (var paint = new io.github.humbleui.skija.Paint()) {
            paint.setColor(0xFFD5D5D5);
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(2, 1355, 1002, 182), paint);
            paint.setColor(0xFFFFFFFF);
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(2, 1357, 1000, 182), paint);
            paint.setColor(0xFF4790E4);
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(689, 1387, 283, 121), paint);
        }
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeXYWH(689, 1387, 283, 121),
                "跳过", 40f, tp);
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var image = ImageEdit.resizeExact(ImageEdit.square(input), 932, 932);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                var tile = ImageEdit.crop(image, 233 * i, 233 * j, 233 * i + 233, 233 * j + 233);
                canvas.drawImage(tile, 21 + i * 243, 372 + j * 243);
            }
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
