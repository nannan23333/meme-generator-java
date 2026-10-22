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

/** 移植自 memes/panda_dragon_figure.rs。 */
@Component
public class PandaDragonFigureMeme extends AbstractMeme {

    public PandaDragonFigureMeme() {
        super(new MemeInfo("panda_dragon_figure",
                new MemeParams(0, 0, 1, 1, List.of("我要玩原神") , List.of(new MemeOption.StringOption("name", "责怪龙", null, null, meme.core.ParserFlags.none()))),
                List.of("熊猫龙图"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 4, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = stringOption(options, "name", "责怪龙");
        String text = texts.get(0);
        var frame = MemeResources.loadImage("panda_dragon_figure/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(0, 470, 470, 550),
                name, 20f, 60f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(0, 0, 550, 120),
                text, 20f, 100f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
