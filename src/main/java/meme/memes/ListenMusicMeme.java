package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
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

/** 移植自 memes/listen_music.rs。 */
@Component
public class ListenMusicMeme extends AbstractMeme {

    public ListenMusicMeme() {
        super(new MemeInfo("listen_music",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("听音乐"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("listen_music/0.png");
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.rotateCrop(
                    ImageEdit.resizeExact(ImageEdit.square(images.get(0).image()), 215, 215), i * 10f);
            canvas.drawImage(image, 100, 100);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
