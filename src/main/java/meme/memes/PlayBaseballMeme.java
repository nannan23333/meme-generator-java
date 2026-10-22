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

/** 移植自 memes/play_baseball.rs。 */
@Component
public class PlayBaseballMeme extends AbstractMeme {

    public PlayBaseballMeme() {
        super(new MemeInfo("play_baseball",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("打棒球"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 150, 150);
        int ballX = 0;
        int ballV = 40;
        int ballAngle = 0;
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            var surface = meme.utils.TextRenderer.newSurface(1000, 300);
            var canvas = surface.getCanvas();
            ballX += ballV;
            ballAngle += ballV > 0 ? 60 : -60;
            if (ballX >= 200) { ballX = 200; ballV = -ballV; }
            else if (ballX <= -200) { ballX = -200; ballV = -ballV; }
            canvas.drawImage(ImageEdit.rotateCrop(img, ballAngle), 425 + ballX, 120);
            int rightIndex = (i >= 3 && i <= 7) ? i - 2 : 0;
            int leftIndex = (i >= 13 && i <= 17) ? i - 12 : 0;
            var right = MemeResources.loadImage("play_baseball/%d.png".formatted(rightIndex));
            var left = ImageEdit.flipHorizontal(MemeResources.loadImage("play_baseball/%d.png".formatted(leftIndex)));
            canvas.drawImage(right, 630, 6);
            canvas.drawImage(left, 0, 6);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
