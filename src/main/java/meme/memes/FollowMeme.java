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

/** 移植自 memes/follow.rs。 */
@Component
public class FollowMeme extends AbstractMeme {

    public FollowMeme() {
        super(new MemeInfo("follow",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("gender", "unknown", List.of("male", "female", "unknown"), null, meme.core.ParserFlags.none()))),
                List.of("关注"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 10, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        if (name == null || name.isEmpty()) {
            String gender = stringOption(options, "gender", "female");
            if (boolOption(options, "male", false)) gender = "male";
            if (boolOption(options, "female", false)) gender = "female";
            name = gender.equals("female") ? "女同" : "男同";
        }
        var nameImage = Text2Image.fromText(name, 60f, null, null, null, null);
        var followImage = Text2Image.fromText("关注了你", 60f, null, 0xFF888888, null, null);
        float textWidth = Math.max(nameImage.longestLine(), followImage.longestLine());
        if (textWidth >= 1000f) {
            throw meme.core.MemeError.textOverLength(name);
        }
        int frameW = 300 + Math.round(textWidth) + 50;
        int frameH = 300;
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        nameImage.drawOnCanvas(canvas, 300, 135 - nameImage.height());
        followImage.drawOnCanvas(canvas, 300, 145);
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var image = ImageEdit.resizeExact(ImageEdit.circle(input), 200, 200);
        canvas.drawImage(image, 50, 50);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
