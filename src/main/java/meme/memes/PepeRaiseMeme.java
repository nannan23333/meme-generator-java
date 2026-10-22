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

/** 移植自 memes/pepe_raise.rs。 */
@Component
public class PepeRaiseMeme extends AbstractMeme {

    public PepeRaiseMeme() {
        super(new MemeInfo("pepe_raise",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("佩佩举"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 8, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 8, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LEFT = {{107, 30}, {107, 30}, {95, 45}, {80, 160}, {80, 160}, {70, 98}};
    private static final int[][] RIGHT = {{320, 145}, {320, 145}, {330, 130}, {300, 50}, {300, 50}, {323, 80}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var leftImg = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 100, 100);
        var rightImg = ImageEdit.resizeExact(ImageEdit.circle(images.get(1).image()), 100, 100);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            var frame = MemeResources.loadImage("pepe_raise/%d.png".formatted(i));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            canvas.drawImage(leftImg, LEFT[i][0], LEFT[i][1]);
            canvas.drawImage(rightImg, RIGHT[i][0], RIGHT[i][1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.06f);
    }
}
