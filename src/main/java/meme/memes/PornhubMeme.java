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

/** 移植自 memes/pornhub.rs。 */
@Component
public class PornhubMeme extends AbstractMeme {

    public PornhubMeme() {
        super(new MemeInfo("pornhub",
                new MemeParams(0, 0, 2, 2, List.of("You", "Tube"), List.of()),
                List.of("ph", "pornhub"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 27, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var leftImg = Text2Image.fromText(texts.get(0), 200f, null, 0xFFFFFFFF, null, null);
        int leftW = Math.round((float) Math.ceil(leftImg.longestLine()));
        int leftH = Math.round((float) Math.ceil(leftImg.height()));
        var rightImg = Text2Image.fromText(texts.get(1), 200f, "NotoSansSC-Bold.ttf",
                0xFF000000, null, null);
        int rightW = Math.round((float) Math.ceil(rightImg.longestLine()));
        int rightH = Math.round((float) Math.ceil(rightImg.height()));

        int paddingX = 20;
        int paddingY = 10;
        int marginX = 50;
        int marginY = 50;
        int textH = Math.max(leftH, rightH);
        int frameW = leftW + rightW + paddingX * 4 + marginX * 2;
        int frameH = textH + paddingY * 2 + marginY * 2;

        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFF000000);
        leftImg.drawOnCanvas(canvas, marginX + paddingX, marginY + paddingY + textH - leftH);
        try (var paint = new io.github.humbleui.skija.Paint().setColor(0xFFF79817)) {
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(
                    marginX + leftW + paddingX * 2, marginY + textH - rightH,
                    rightW + paddingX * 2, rightH + paddingY * 2, 20f, 20f), paint);
        }
        rightImg.drawOnCanvas(canvas, marginX + leftW + paddingX * 3,
                marginY + paddingY + textH - rightH);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
