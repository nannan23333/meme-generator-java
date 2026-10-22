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

/** 移植自 memes/interview.rs。 */
@Component
public class InterviewMeme extends AbstractMeme {

    public InterviewMeme() {
        super(new MemeInfo("interview",
                new MemeParams(1, 2, 0, 1, List.of("采访大佬经验"), List.of()),
                List.of("采访"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "采访大佬经验" : texts.get(0);
        var surface = meme.utils.TextRenderer.newSurface(600, 310);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(20, 200, 580, 310),
                text, 20f, 50f,
                meme.utils.TextRenderer.params().fill(0xFF000000));
        var huaji = MemeResources.loadImage("interview/huaji.png");
        var microphone = MemeResources.loadImage("interview/microphone.png");
        var selfImg = images.size() == 2 ? images.get(0).image() : huaji;
        var userImg = images.size() == 2 ? images.get(1).image() : images.get(0).image();
        selfImg = ImageEdit.resizeExact(ImageEdit.square(selfImg), 124, 124);
        userImg = ImageEdit.resizeExact(ImageEdit.square(userImg), 124, 124);
        canvas.drawImage(microphone, 330, 103);
        canvas.drawImage(selfImg, 419, 40);
        canvas.drawImage(userImg, 57, 40);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
