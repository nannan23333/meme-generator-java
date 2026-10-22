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

/** 移植自 memes/wallpaper.rs。 */
@Component
public class WallpaperMeme extends AbstractMeme {

    public WallpaperMeme() {
        super(new MemeInfo("wallpaper",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("墙纸"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 22, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("wallpaper/%02d.png".formatted(outIdx));
            if (outIdx < 8) {
                return frame;
            }
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var img = ImageEdit.resizeFit(images.get(0).image(), 515, 383, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 176, -9);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 20, 0.07f);
    }
}
