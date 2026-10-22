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

/** 移植自 memes/call_110.rs。 */
@Component
public class Call110Meme extends AbstractMeme {

    public Call110Meme() {
        super(new MemeInfo("call_110",
                new MemeParams(2, 2, 0, 0, List.of(), List.of()),
                List.of("遇到困难请拨打"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 8, 26, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var baseSurface = meme.utils.TextRenderer.newSurface(900, 500);
        var bcanvas = baseSurface.getCanvas();
        bcanvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextArea(bcanvas,
                io.github.humbleui.types.Rect.makeLTRB(0, 0, 900, 200),
                "遇到困难请拨打", 100f, meme.utils.TextRenderer.params());
        var frame = baseSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input1 -> {
            var surface = meme.utils.TextRenderer.newSurface(900, 500);
            var canvas = surface.getCanvas();
            canvas.drawImage(frame, 0, 0);
            var input2 = images.get(1).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(1).gifFrames().get(0).image())
                    : images.get(1).image();
            var image1 = ImageEdit.resizeExact(ImageEdit.square(input1), 250, 250);
            var image0 = ImageEdit.resizeExact(ImageEdit.square(input2), 250, 250);
            canvas.drawImage(image1, 50, 200);
            canvas.drawImage(image1, 325, 200);
            canvas.drawImage(image0, 600, 200);
            return surface.makeImageSnapshot();
        });
    }
}
