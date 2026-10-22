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

/** 移植自 memes/wujing.rs。 */
@Component
public class WujingMeme extends AbstractMeme {

    public WujingMeme() {
        super(new MemeInfo("wujing",
                new MemeParams(0, 0, 2, 4, List.of("不买华为不是", "人") , List.of()),
                List.of("吴京中国"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("wujing/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var whiteStroke = meme.utils.TextRenderer.params()
                .fill(0xFFFFFFFF).stroke(0.1f, 0xFF000000);
        if (texts.size() == 2) {
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(20, 560, 350, 690),
                    texts.get(0), 50f, 100f, whiteStroke.align(meme.utils.TextRenderer.Params.Align.RIGHT));
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(610, 540, 917, 670),
                    texts.get(1), 50f, 100f, whiteStroke);
        } else {
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(50, 430, 887, 550),
                    texts.get(0), 50f, 100f, whiteStroke);
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(20, 560, 350, 690),
                    texts.get(1), 50f, 100f, whiteStroke.align(meme.utils.TextRenderer.Params.Align.RIGHT));
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                    io.github.humbleui.types.Rect.makeLTRB(610, 540, 917, 670),
                    texts.get(2), 50f, 100f, whiteStroke);
            if (texts.size() >= 4) {
                meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                        io.github.humbleui.types.Rect.makeLTRB(50, 680, 887, 810),
                        texts.get(3), 50f, 100f,
                        whiteStroke.align(meme.utils.TextRenderer.Params.Align.CENTER));
            }
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
