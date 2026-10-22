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

/** 移植自 memes/pixelate.rs。 */
@Component
public class PixelateMeme extends AbstractMeme {

    public PixelateMeme() {
        super(new MemeInfo("pixelate",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.IntegerOption("number", 10, 2, 50, null, meme.core.ParserFlags.none()))),
                List.of("像素化"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int num = 10;
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = Math.max(2, Math.min(50, n.intValue()));
        }
        int fnum = num;
        return InputImage.makePngOrGif(images.get(0), input ->
                ImageEdit.pixelate(input, fnum));
    }
}
