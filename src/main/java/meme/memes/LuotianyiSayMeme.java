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

/** 移植自 memes/luotianyi_say.rs。 */
@Component
public class LuotianyiSayMeme extends AbstractMeme {

    public LuotianyiSayMeme() {
        super(new MemeInfo("luotianyi_say",
                new MemeParams(0, 0, 1, 1, List.of("好想去海边啊～"), List.of()),
                List.of("洛天依说", "天依说"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 7, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 7, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("luotianyi_say/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(520, 0, frame.getWidth() - 20, 220),
                text, 40f, 140f,
                meme.utils.TextRenderer.params().fill(0xFF66CCFF));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
