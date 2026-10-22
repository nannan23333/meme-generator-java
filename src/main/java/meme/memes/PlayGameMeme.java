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

/** 移植自 memes/play_game.rs。 */
@Component
public class PlayGameMeme extends AbstractMeme {

    public PlayGameMeme() {
        super(new MemeInfo("play_game",
                new MemeParams(1, 1, 0, 1, List.of("来玩休闲游戏啊"), List.of()),
                List.of("玩游戏"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "来玩休闲游戏啊" : texts.get(0);
        var frame = MemeResources.loadImage("play_game/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(20, frame.getHeight() - 70, frame.getWidth() - 20, frame.getHeight()),
                text, 25f, 40f,
                meme.utils.TextRenderer.params().stroke(0.16f, 0xFFFFFFFF));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var screen = ImageEdit.perspective(
                    ImageEdit.resizeFit(input, 230, 160, ImageEdit.Fit.COVER),
                    0, 5, 227, 0, 216, 150, 0, 165);
            canvas.drawImage(ImageEdit.rotate(screen, -9f), 161, 117);
            canvas.drawImage(composed, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
