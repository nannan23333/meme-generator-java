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

/** 移植自 memes/read_book.rs。 */
@Component
public class ReadBookMeme extends AbstractMeme {

    public ReadBookMeme() {
        super(new MemeInfo("read_book",
                new MemeParams(1, 1, 0, 1, List.of("エロ本"), List.of()),
                List.of("看书"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 25, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "エロ本" : texts.get(0);
        var textSurface = meme.utils.TextRenderer.newSurface(775, 300);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(0, 50, 775, 250),
                text, 50f, 150f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        var textImage = ImageEdit.rotate(textSurface.makeImageSnapshot(), 88f);
        var frame = MemeResources.loadImage("read_book/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        var img = ImageEdit.perspective(
                ImageEdit.resizeFit(images.get(0).image(), 1000, 1100, ImageEdit.Fit.COVER),
                0, 108, 1092, 0, 1023, 1134, 29, 1134);
        canvas.drawImage(img, 1138, 1172);
        canvas.drawImage(frame, 0, 0);
        canvas.drawImage(textImage, 826, 1496);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
