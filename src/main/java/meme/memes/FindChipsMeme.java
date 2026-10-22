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

/** 移植自 memes/find_chips.rs。 */
@Component
public class FindChipsMeme extends AbstractMeme {

    public FindChipsMeme() {
        super(new MemeInfo("find_chips",
                new MemeParams(0, 0, 4, 4, List.of("我们要飞向何方", "我打算待会去码头整点薯条", "我说的是归根结底，活着是为了什么", "为了待会去码头整点薯条") , List.of()),
                List.of("整点薯条"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 11, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] RECTS = {
            {405, 54, 530, 130}, {570, 62, 667, 160}, {65, 400, 325, 463}, {430, 400, 630, 470}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("find_chips/0.jpg");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        for (int i = 0; i < 4; i++) {
            int[] r = RECTS[i];
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeLTRB(r[0], r[1], r[2], r[3]),
                    texts.get(i), 12f, 30f, meme.utils.TextRenderer.params());
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
