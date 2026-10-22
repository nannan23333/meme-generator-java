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

/** 移植自 memes/keep_your_money.rs。 */
@Component
public class KeepYourMoneyMeme extends AbstractMeme {

    public KeepYourMoneyMeme() {
        super(new MemeInfo("keep_your_money",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("character", null, List.of("arona", "plana"), null, meme.core.ParserFlags.none()))),
                List.of("压岁钱不要交给"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2025, 2, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 2, 2, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String[] CHARACTERS = {"arona", "plana"};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String character = stringOption(options, "character", null);
        if (character == null) {
            character = CHARACTERS[(int) (Math.random() * CHARACTERS.length)];
        }
        var frame = MemeResources.loadImage("keep_your_money/" + character + ".png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(input, 500, 640, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(image, 0, 440);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
