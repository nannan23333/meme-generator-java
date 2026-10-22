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

/** 移植自 memes/atri_pillow.rs。 */
@Component
public class AtriPillowMeme extends AbstractMeme {

    public AtriPillowMeme() {
        super(new MemeInfo("atri_pillow",
                new MemeParams(0, 0, 1, 1, List.of("ATRI") , List.of(new MemeOption.StringOption("mode", null, List.of("yes", "no"), null, meme.core.ParserFlags.none()))),
                List.of("亚托莉枕头"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String mode = stringOption(options, "mode", null);
        if (boolOption(options, "yes", false)) mode = "yes";
        if (boolOption(options, "no", false)) mode = "no";
        if (mode == null) {
            mode = Math.random() < 0.5 ? "yes" : "no";
        }
        String text = texts.get(0);
        int textColor = mode.equals("yes") ? 0x4DFF0000 : 0x4D004DFF;
        var frame = MemeResources.loadImage("atri_pillow/" + mode + ".png");
        var surface = meme.utils.TextRenderer.newSurface(300, 150);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                surface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(20, 20, 280, 130),
                text, 30f, 120f,
                meme.utils.TextRenderer.params().fill(textColor).fontFamilies("FZShaoEr-M11S"));
        var textImage = ImageEdit.rotate(surface.makeImageSnapshot(), 4f);

        var surface2 = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface2.getCanvas();
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 302, 288);
        var border = MemeResources.loadImage("atri_pillow/border.png");
        canvas.drawImage(border, 0, 416);
        return ImageEdit.encodePng(surface2.makeImageSnapshot());
    }
}
