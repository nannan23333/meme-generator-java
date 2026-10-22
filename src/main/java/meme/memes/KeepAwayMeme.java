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

/** 移植自 memes/keep_away.rs。 */
@Component
public class KeepAwayMeme extends AbstractMeme {

    public KeepAwayMeme() {
        super(new MemeInfo("keep_away",
                new MemeParams(1, 8, 0, 1, List.of("如何提高社交质量 : \n远离以下头像的人") , List.of()),
                List.of("远离"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 9, 4, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "离我远点" : texts.get(0);
        var surface = meme.utils.TextRenderer.newSurface(400, 290);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(10, 10, 390, 80),
                text, 20f, 40f, meme.utils.TextRenderer.params());
        int numPerUser = 8 / images.size();
        int count = 0;
        for (InputImage image : images) {
            for (int n = 0; n < numPerUser; n++) {
                var img = ImageEdit.resizeFit(ImageEdit.square(image.image()), 100, 100, ImageEdit.Fit.COVER);
                var transformed = n < 4
                        ? ImageEdit.rotate(img, n * 90f)
                        : ImageEdit.rotate(ImageEdit.flipHorizontal(img), (n - 4) * 90f);
                int y = count < 4 ? 90 : 190;
                canvas.drawImage(transformed, count % 4 * 100, y);
                count++;
            }
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
