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

/** 移植自 memes/youtube.rs。 */
@Component
public class YoutubeMeme extends AbstractMeme {

    public YoutubeMeme() {
        super(new MemeInfo("youtube",
                new MemeParams(0, 0, 2, 2, List.of("Porn", "Hub"), List.of()),
                List.of("yt", "youtube"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 7, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var leftImg = Text2Image.fromText(texts.get(0), 200f, null, 0xFF000000, null, null);
        var rightImg = Text2Image.fromText(texts.get(1), 200f, null, 0xFFFFFFFF, null, null);
        float rightW0 = Math.max((float) Math.ceil(rightImg.longestLine()), 400f);
        rightImg.layout(rightW0);

        int leftW = Math.round((float) Math.ceil(leftImg.longestLine()));
        int leftH = Math.round((float) Math.ceil(leftImg.height()));
        int rightW = Math.round(rightW0);
        int rightH = Math.round((float) Math.ceil(rightImg.height()));

        int leftPaddingX = 30;
        int rightPaddingX = 50;
        int paddingY = 20;
        int marginX = 50;
        int marginY = 50;
        int frameW = leftW + rightW + leftPaddingX * 2 + rightPaddingX * 2 + marginX * 2;
        int frameH = Math.max(leftH, rightH) + paddingY * 2 + marginY * 2;

        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);

        var corner0 = MemeResources.loadImage("youtube/corner.png");
        float ratio = rightH / (2f * corner0.getHeight());
        var corner = ImageEdit.resizeExact(corner0,
                Math.round(corner0.getWidth() * ratio), Math.round(corner0.getHeight() * ratio));

        int x0 = marginX + leftW + leftPaddingX * 2;
        int y0 = frameH - rightH - paddingY * 2 - marginY;
        int x1 = frameW - corner.getWidth() - marginX;
        int y1 = frameH - corner.getHeight() - marginY;

        canvas.drawImage(corner, x0, y0);
        canvas.drawImage(ImageEdit.flipVertical(corner), x0, y1);
        canvas.drawImage(ImageEdit.flipHorizontal(corner), x1, y0);
        canvas.drawImage(ImageEdit.flipVertical(ImageEdit.flipHorizontal(corner)), x1, y1);

        float radius = rightH / 2f;
        try (var paint = new io.github.humbleui.skija.Paint().setColor(0xFFE62117)) {
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(
                    x0, y0, rightW + rightPaddingX * 2, rightH + paddingY * 2, radius, radius), paint);
        }
        leftImg.drawOnCanvas(canvas, marginX + leftPaddingX, frameH - leftH - paddingY - marginY);
        rightImg.drawOnCanvas(canvas, x0 + rightPaddingX, frameH - rightH - paddingY - marginY);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
