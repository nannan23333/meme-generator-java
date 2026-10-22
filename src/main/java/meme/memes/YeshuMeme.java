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

/** 移植自 memes/yeshu.rs。 */
@Component
public class YeshuMeme extends AbstractMeme {

    public YeshuMeme() {
        super(new MemeInfo("yeshu",
                new MemeParams(0, 0, 8, 8, List.of("椰子特产在海南", "正宗", "椰树", "29年", "坚持在海南岛", "用新鲜椰子肉", "鲜榨", "不用椰浆\n不加香精当生榨"), List.of()),
                List.of("椰树椰汁"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 5, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int yellow = 0xFFFEEF50;
        int blue = 0xFF0021FC;
        int red = 0xFFFD0000;
        int frameW = 600;
        int frameH = 1200;
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(yellow);
        try (var p = new io.github.humbleui.skija.Paint()) {
            p.setColor(0xFF000000);
            canvas.drawRect(io.github.humbleui.types.Rect.makeLTRB(4, 4, frameW - 4, frameH - 4), p);
            p.setColor(yellow);
            canvas.drawRect(io.github.humbleui.types.Rect.makeLTRB(28, 16, frameW - 28, 1000), p);
            p.setColor(0xFF000000);
            canvas.drawRect(io.github.humbleui.types.Rect.makeLTRB(34, 88, frameW - 34, 999), p);
            p.setColor(blue);
            canvas.drawRect(io.github.humbleui.types.Rect.makeLTRB(36, 90, frameW - 36, 997), p);
        }
        int px = 36;
        var bold = meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf");
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(px, 16, frameW - px, 88),
                texts.get(0), 40f, 70f,
                meme.utils.TextRenderer.params().fill(0xFF000000).fontFamilies("NotoSansSC-Bold.ttf"));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(px, 90, frameW - px, 170),
                texts.get(1), 40f, 70f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).fontFamilies("NotoSansSC-Bold.ttf"));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(px, 390, frameW - px, 590),
                texts.get(3), 100f, 200f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF)
                        .strokePx(10f, 0xFF000000).fontFamilies("NotoSansSC-Bold.ttf"));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(px, 580, frameW - px, 660),
                texts.get(4), 40f, 70f,
                meme.utils.TextRenderer.params().fill(yellow).fontFamilies("NotoSansSC-Bold.ttf"));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(px, 740, frameW - px, 1000),
                texts.get(6), 150f, 250f,
                meme.utils.TextRenderer.params().fill(yellow)
                        .strokePx(12f, 0xFF000000).fontFamilies("NotoSansSC-Bold.ttf"));
        // 红色大框（texts[2]）
        try (var p = new io.github.humbleui.skija.Paint()) {
            p.setColor(yellow);
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(38, 170, frameW - 76, 233, 30f, 30f), p);
            p.setColor(red);
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(41, 173, frameW - 82, 227, 28f, 28f), p);
        }
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(51, 158, frameW - 51, 400),
                texts.get(2), 100f, 230f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).fontFamilies("NotoSansSC-Bold.ttf"));
        // 小框（texts[5]）
        try (var p = new io.github.humbleui.skija.Paint()) {
            p.setColor(red);
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(32, 668, frameW - 64, 92, 5f, 5f), p);
            p.setColor(yellow);
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(36, 672, frameW - 72, 84, 1f, 1f), p);
        }
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(36, 667, frameW - 36, 756),
                texts.get(5), 50f, 100f,
                meme.utils.TextRenderer.params().fill(red).fontFamilies("NotoSansSC-Bold.ttf"));
        // 底部大框（texts[7]）
        try (var p = new io.github.humbleui.skija.Paint()) {
            p.setColor(yellow);
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(10, 1000, frameW - 20, 188, 8f, 8f), p);
            p.setColor(red);
            canvas.drawRRect(io.github.humbleui.types.RRect.makeXYWH(12, 1002, frameW - 24, 184, 6f, 6f), p);
        }
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(22, 992, frameW - 22, 1186),
                texts.get(7), 50f, 180f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).fontFamilies("NotoSansSC-Bold.ttf"));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
