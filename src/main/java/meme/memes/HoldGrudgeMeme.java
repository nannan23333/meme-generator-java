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

/** 移植自 memes/hold_grudge.rs。 */
@Component
public class HoldGrudgeMeme extends AbstractMeme {

    public HoldGrudgeMeme() {
        super(new MemeInfo("hold_grudge",
                new MemeParams(0, 0, 1, 1, List.of("群友不发涩图"), List.of(new MemeOption.StringOption("time", null, null, null, meme.core.ParserFlags.none()))),
                List.of("记仇"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 12, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        java.time.LocalDate time = java.time.LocalDate.now();
        String timeSet = stringOption(options, "time", null);
        if (timeSet != null) {
            try {
                time = java.time.LocalDate.parse(timeSet);
            } catch (java.time.format.DateTimeParseException e) {
                throw meme.core.MemeError.feedback("时间格式错误，正确格式为：" + time);
            }
        }
        String date = time.format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
        String text = date + " 晴\n" + texts.get(0) + "\n这个仇我先记下了";
        var textImg = Text2Image.fromText(text, 45f, null, null, null, null);
        textImg.layout(440f);
        if (textImg.height() > 500f) {
            throw meme.core.MemeError.textOverLength(texts.get(0));
        }
        var frame = MemeResources.loadImage("hold_grudge/0.png");
        int frameH = frame.getHeight() + Math.round(textImg.height()) + 20;
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        canvas.drawImage(frame, 0, 0);
        textImg.drawOnCanvas(canvas, 30, frame.getHeight() + 5);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
