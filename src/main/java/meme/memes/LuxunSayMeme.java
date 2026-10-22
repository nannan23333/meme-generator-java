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

/** 移植自 memes/luxun_say.rs。 */
@Component
public class LuxunSayMeme extends AbstractMeme {

    public LuxunSayMeme() {
        super(new MemeInfo("luxun_say",
                new MemeParams(0, 0, 1, 1, List.of("我没有说过这句话") , List.of()),
                List.of("鲁迅说", "鲁迅说过"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var frame = MemeResources.loadImage("luxun_say/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(40, frame.getHeight() - 200, frame.getWidth() - 40, frame.getHeight() - 100),
                text, 30f, 40f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        // 右下角落款（Rust: canvas.draw_text((320, 400), "--鲁迅", 30.0, ...)）
        meme.utils.TextRenderer.drawText(canvas, 320f, 400f, "--鲁迅", 30f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
