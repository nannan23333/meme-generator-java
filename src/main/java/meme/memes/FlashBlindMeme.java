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

/** 移植自 memes/flash_blind.rs。 */
@Component
public class FlashBlindMeme extends AbstractMeme {

    public FlashBlindMeme() {
        super(new MemeInfo("flash_blind",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("闪瞎"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 5, 5, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            return switch (outIdx) {
                case 0 -> input;
                case 1 -> ImageEdit.invert(input);
                case 2 -> {
                    int w = input.getWidth();
                    int h = input.getHeight();
                    int pw = w / 20;
                    int ph = h / 20;
                    yield ImageEdit.resizeExact(ImageEdit.crop(input, pw, ph, w - pw, h - ph), w, h);
                }
                default -> ImageEdit.invert(input);
            };
        }, 4, 0.03f);
    }
}
