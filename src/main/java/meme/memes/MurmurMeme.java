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

/** 移植自 memes/murmur.rs。 */
@Component
public class MurmurMeme extends AbstractMeme {

    public MurmurMeme() {
        super(new MemeInfo("murmur",
                new MemeParams(0, 0, 1, 1, List.of("你的假期余额不足"), List.of()),
                List.of("低语"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 12, 31, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("image_subtitle/murmur.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(10, frame.getHeight() - 55,
                        frame.getWidth() - 10, frame.getHeight() - 5),
                texts.get(0), 15.0f, 40.0f, meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
