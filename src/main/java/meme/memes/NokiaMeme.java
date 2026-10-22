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

/** 移植自 memes/nokia.rs。 */
@Component
public class NokiaMeme extends AbstractMeme {

    public NokiaMeme() {
        super(new MemeInfo("nokia",
                new MemeParams(0, 0, 1, 1, List.of("无内鬼，继续交易"), List.of()),
                List.of("诺基亚", "有内鬼"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 12, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("nokia/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.save();
        // 绕 (260,300) 旋转：skija 没有 rotate(deg,x,y) 重载，用 translate/rotate/translate 还原
        canvas.translate(260, 300);
        canvas.rotate(9.3f);
        canvas.translate(-260, -300);
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeXYWH(270, 210, 730, 90),
                text.codePoints().count() + "/900", 70f,
                meme.utils.TextRenderer.params().fontFamilies("FZXS14")
                        .align(meme.utils.TextRenderer.Params.Align.RIGHT)
                        .fill(0xFF81D4FA));
        var textImg = Text2Image.fromText(text, 70f, "FZXS14", null, null, null);
        textImg.layout(730f);
        if (textImg.height() > 450f) {
            throw meme.core.MemeError.textOverLength(text);
        }
        textImg.drawOnCanvas(canvas, 270, 320);
        canvas.restore();
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
