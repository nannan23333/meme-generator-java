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

/** 移植自 memes/ba_say.rs。 */
@Component
public class BaSayMeme extends AbstractMeme {

    public BaSayMeme() {
        super(new MemeInfo("ba_say",
                new MemeParams(0, 0, 1, 1, List.of("那我问你"), List.of(new MemeOption.StringOption("character", null, List.of("arisu", "izuna", "key", "kokona", "mari", "sena", "yuuka"), null, meme.core.ParserFlags.none()), new MemeOption.StringOption("position", null, List.of("left", "right"), null, meme.core.ParserFlags.none()))),
                List.of("ba说"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 12, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String[] CHARACTERS = {"arisu", "izuna", "key", "kokona", "mari", "sena", "yuuka"};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String character = stringOption(options, "character", null);
        if (character == null) {
            character = CHARACTERS[(int) (Math.random() * CHARACTERS.length)];
        }
        String position = stringOption(options, "position", null);
        if (boolOption(options, "left", false)) position = "left";
        if (boolOption(options, "right", false)) position = "right";
        if (position == null) {
            position = Math.random() < 0.5 ? "left" : "right";
        }
        String text = texts.get(0);
        var frame = MemeResources.loadImage("ba_say/" + character + ".png");
        if (position.equals("left")) {
            frame = ImageEdit.flipHorizontal(frame);
        }
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var rect = position.equals("left")
                ? io.github.humbleui.types.Rect.makeLTRB(60, 0, 580, 200)
                : io.github.humbleui.types.Rect.makeLTRB(500, 0, 1020, 200);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas, rect, text, 20f, 100f,
                meme.utils.TextRenderer.params());
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
