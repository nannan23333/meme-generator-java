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

/** 移植自 memes/turn.rs。 */
@Component
public class TurnMeme extends AbstractMeme {

    public TurnMeme() {
        super(new MemeInfo("turn",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("转"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 1, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 9, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int direction = Math.random() < 0.5 ? -1 : 1;
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            float angle = outIdx * 10f * direction;
            return ImageEdit.resizeExact(ImageEdit.rotateCrop(ImageEdit.circle(images.get(0).image()), angle), 250, 250);
        }, 36, 0.05f);
    }
}
