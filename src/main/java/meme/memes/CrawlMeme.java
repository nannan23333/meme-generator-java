package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** crawl 爬：移植自 memes/crawl.rs。 */
@Component
public class CrawlMeme extends AbstractMeme {

    public CrawlMeme() {
        super(new MemeInfo("crawl",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.IntegerOption("number", null, 1, 92, null, meme.core.ParserFlags.none()))),
                List.of("爬"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 5, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int num = 1 + (int) (Math.random() * 92);
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = Math.max(1, Math.min(92, n.intValue()));
        }
        var frame = MemeResources.loadImage("crawl/%02d.jpg".formatted(num));
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            // 对齐 Rust：frame 作背景，头像压在 (0,400) 上
            canvas.drawImage(frame, 0, 0);
            var image = ImageEdit.resizeExact(ImageEdit.circle(input), 100, 100);
            canvas.drawImage(image, 0, 400);
            return surface.makeImageSnapshot();
        });
    }
}
