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

/** 移植自 memes/note_for_leave.rs。 */
@Component
public class NoteForLeaveMeme extends AbstractMeme {

    public NoteForLeaveMeme() {
        super(new MemeInfo("note_for_leave",
                new MemeParams(1, 1, 0, 1, List.of("想玩原神"), List.of(new MemeOption.StringOption("time", null, null, null, meme.core.ParserFlags.none()))),
                List.of("请假条"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 27, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 27, 0, 0), ZoneOffset.ofHours(8))));
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
        String name = images.get(0).name();
        String text = texts.isEmpty() ? "想玩原神" : texts.get(0);
        var surface = meme.utils.TextRenderer.newSurface(800, 950);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(40, 20, 760, 180),
                "请假条", 100f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var line1 = Text2Image.fromBbcode("本人[u] " + name + " [/u]因", 50f,
                null, null, null, null);
        if (line1.height() > 90f) {
            throw meme.core.MemeError.textOverLength(name);
        }
        line1.drawOnCanvas(canvas, 40, 180);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(40, 300, 285, 700),
                text, 40f, 90f, meme.utils.TextRenderer.params().fill(0xFFFF0000));
        String leaveText = "于[u] " + time.getYear() + " [/u]年[u] " + time.getMonthValue()
                + " [/u]月[u] " + time.getDayOfMonth() + " [/u]日请假一天";
        Text2Image.fromBbcode(leaveText, 50f, null, null, null, null)
                .drawOnCanvas(canvas, 40, 720);
        meme.utils.TextRenderer.drawText(canvas, 40, 800, "望领导批准！！！", 75f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var image = ImageEdit.resizeFit(input, 450, 400, ImageEdit.Fit.CONTAIN);
        canvas.drawImage(image, 300, 290);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
