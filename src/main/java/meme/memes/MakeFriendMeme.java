package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/make_friend.rs。 */
@Component
public class MakeFriendMeme extends AbstractMeme {

    public MakeFriendMeme() {
        super(new MemeInfo("make_friend",
                new MemeParams(1, 1, 0, 1, List.of(), List.of()),
                List.of("交个朋友"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 9, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        var frame = MemeResources.loadImage("make_friend/0.png");
        float angle = -9f;
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var squared = ImageEdit.square(input);
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(ImageEdit.rotate(ImageEdit.resizeExact(squared, 250, 250), angle),
                743, frame.getHeight() - 155);
        canvas.drawImage(ImageEdit.rotate(ImageEdit.resizeExact(squared, 55, 55), angle),
                836, frame.getHeight() - 278);
        var nameImg = Text2Image.fromText(name, 20f, null, 0xFFFFFFFF, null, null);
        if (nameImg.longestLine() > 230f) {
            throw meme.core.MemeError.textOverLength(name);
        }
        canvas.drawImage(frame, 0, 0);
        canvas.save();
        canvas.translate(710, 710);
        canvas.rotate(angle);
        nameImg.drawOnCanvas(canvas, 0, -nameImg.height() / 2f);
        canvas.restore();
        var composed = surface.makeImageSnapshot();
        var surface2 = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
        var canvas2 = surface2.getCanvas();
        canvas2.clear(0xFFFFFFFF);
        var bg = ImageEdit.resizeWidth(input, composed.getWidth());
        canvas2.drawImage(bg, 0, 0);
        canvas2.drawImage(composed, 0, bg.getHeight() - composed.getHeight());
        return ImageEdit.encodePng(surface2.makeImageSnapshot());
    }
}
