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

/** 移植自 memes/certificate.rs。 */
@Component
public class CertificateMeme extends AbstractMeme {

    public CertificateMeme() {
        super(new MemeInfo("certificate",
                new MemeParams(0, 0, 3, 4, List.of("小王", "优秀学生", "一年一班"), List.of(new MemeOption.StringOption("time", null, null, null, meme.core.ParserFlags.none()))),
                List.of("奖状", "证书"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 12, 3, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 12, 3, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        java.time.LocalDate time = java.time.LocalDate.now();
        String timeSet = stringOption(options, "time", null);
        if (timeSet != null) {
            try {
                time = java.time.LocalDate.parse(timeSet);
            } catch (java.time.format.DateTimeParseException e) {
                throw meme.core.MemeError.feedback("时间格式错误，正确格式为：" + time.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
        var frame = MemeResources.loadImage("certificate/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(340, 660, 840, 800),
                texts.get(0), 20f, 80f, meme.utils.TextRenderer.params());
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(565, 1040, 2100, 1320),
                texts.get(1), 60f, 120f, meme.utils.TextRenderer.params().fill(0xFFFF0000));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(1500, 1400, 2020, 1520),
                texts.get(2), 20f, 60f, meme.utils.TextRenderer.params());
        String text = texts.size() >= 4 ? texts.get(3) : "　　在本学年第一学期中表现优秀，被我校决定评为";
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(450, 850, 2270, 1080),
                text, 40f, 80f, meme.utils.TextRenderer.params());
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(1565, 1520, 1700, 1600),
                time.format(java.time.format.DateTimeFormatter.ofPattern("yyyy")), 50f,
                meme.utils.TextRenderer.params());
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(1752, 1520, 1816, 1600),
                time.format(java.time.format.DateTimeFormatter.ofPattern("MM")), 50f,
                meme.utils.TextRenderer.params());
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(1865, 1520, 1930, 1600),
                time.format(java.time.format.DateTimeFormatter.ofPattern("dd")), 50f,
                meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
