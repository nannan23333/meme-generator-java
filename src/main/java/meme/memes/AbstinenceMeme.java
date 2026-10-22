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

/** 移植自 memes/abstinence.rs。 */
@Component
public class AbstinenceMeme extends AbstractMeme {

    public AbstinenceMeme() {
        super(new MemeInfo("abstinence",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("time", null, null, null, meme.core.ParserFlags.none()))),
                List.of("戒导"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 13, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 14, 0, 0), ZoneOffset.ofHours(8))));
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
        var frame = MemeResources.loadImage("abstinence/base.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        // 戒导人（bbcode 自动字号 10~20，按框宽排版并垂直居中）+ 日期（固定 20）
        String l1 = "戒导人：[u]" + name + "[/u]";
        Text2Image.drawBbcodeAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(150, 650, 760, 800), l1, 10f, 20f, null);
        String l2 = "[u] " + time.getYear() + " [/u]年[u] " + time.getMonthValue()
                + " [/u]月[u] " + time.getDayOfMonth() + " [/u]日";
        Text2Image.drawBbcodeArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(150, 750, 760, 800), l2, 20f, null);
        var stamp = MemeResources.loadImage("abstinence/stamp.png");
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var image = ImageEdit.resizeFit(input, 270, 360, ImageEdit.Fit.CONTAIN);
        canvas.drawImage(image, 80, 380);
        canvas.drawImage(stamp, 310, 660);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
