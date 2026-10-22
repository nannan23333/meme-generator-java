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

/** 移植自 memes/scroll.rs。 */
@Component
public class ScrollMeme extends AbstractMeme {

    public ScrollMeme() {
        super(new MemeInfo("scroll",
                new MemeParams(0, 0, 1, 1, List.of("你们说话啊"), List.of()),
                List.of("滚屏"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var textImg = Text2Image.fromText(text, 40f, null, null, null, null);
        if (textImg.longestLine() > 600f) {
            textImg.layout(600f);
        }
        if (textImg.height() > 200f) {
            throw meme.core.MemeError.textOverLength(text);
        }
        int textW = Math.round((float) Math.ceil(textImg.longestLine()));
        int textH = Math.round((float) Math.ceil(textImg.height()));

        int boxW = textW + 140;
        int boxH = Math.max(textH + 103, 150);
        var boxSurface = meme.utils.TextRenderer.newSurface(boxW, boxH);
        var bc = boxSurface.getCanvas();
        bc.clear(0xFFEAEDF4);
        bc.drawImage(MemeResources.loadImage("scroll/corner1.png"), 0, 0);
        bc.drawImage(MemeResources.loadImage("scroll/corner2.png"), 0, boxH - 75);
        bc.drawImage(MemeResources.loadImage("scroll/corner3.png"), textW + 70, 0);
        bc.drawImage(MemeResources.loadImage("scroll/corner4.png"), textW + 70, boxH - 75);
        try (var white = new io.github.humbleui.skija.Paint().setColor(0xFFFFFFFF)) {
            bc.drawRect(io.github.humbleui.types.Rect.makeXYWH(70, 20, textW, boxH - 40), white);
            bc.drawRect(io.github.humbleui.types.Rect.makeXYWH(27, 75, textW + 88, boxH - 150), white);
        }
        textImg.drawOnCanvas(bc, 70, 17 + (boxH - 40 - textH) / 2f);
        var boxImage = boxSurface.makeImageSnapshot();

        var dialogSurface = meme.utils.TextRenderer.newSurface(boxW, boxH * 4);
        var dc = dialogSurface.getCanvas();
        dc.clear(0xFFEAEDF4);
        for (int i = 0; i < 4; i++) {
            dc.drawImage(boxImage, 0, boxH * i);
        }
        var dialog = dialogSurface.makeImageSnapshot();

        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        int num = 30;
        int dy = dialog.getHeight() / num;
        for (int i = 0; i < num; i++) {
            var surface = meme.utils.TextRenderer.newSurface(dialog.getWidth(), dialog.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(dialog, 0, -dy * i);
            canvas.drawImage(dialog, 0, dialog.getHeight() - dy * i);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
