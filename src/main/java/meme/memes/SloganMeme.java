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

/** 移植自 memes/slogan.rs。 */
@Component
public class SloganMeme extends AbstractMeme {

    public SloganMeme() {
        super(new MemeInfo("slogan",
                new MemeParams(0, 0, 6, 6, List.of("我们是谁？", "浙大人！", "到浙大来做什么？", "混！", "将来毕业后要做什么样的人？", "混混！") , List.of()),
                List.of("口号"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 7, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 7, 22, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] RECTS = {
            {10, 0, 294, 50}, {316, 0, 602, 50}, {10, 230, 294, 280},
            {316, 230, 602, 280}, {10, 455, 294, 505}, {316, 455, 602, 505}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("slogan/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        for (int i = 0; i < 6; i++) {
            int[] r = RECTS[i];
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeLTRB(r[0], r[1], r[2], r[3]),
                    texts.get(i), 15f, 40f, meme.utils.TextRenderer.params());
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
