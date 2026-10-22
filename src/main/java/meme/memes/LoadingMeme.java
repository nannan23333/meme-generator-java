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

/** 移植自 memes/loading.rs。 */
@Component
public class LoadingMeme extends AbstractMeme {

    public LoadingMeme() {
        super(new MemeInfo("loading",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("加载中"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 12, 29, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img0 = images.get(0).image();
        int imgW = 500;
        var imgBig = ImageEdit.gaussianBlur(ImageEdit.resizeWidth(img0, imgW), 3f, 3f);
        int h1 = imgBig.getHeight();
        int imgWSmall = 100;
        int h2 = Math.max(80, Math.round((float) h1 / imgW * imgWSmall));
        int frameH = h1 + h2 + 10;
        var surface = meme.utils.TextRenderer.newSurface(imgW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        canvas.drawImage(imgBig, 0, 0);
        try (var paint = new io.github.humbleui.skija.Paint().setColor(0x4D000000)) {
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(0, 0, imgBig.getWidth(), h1), paint);
        }
        var icon = MemeResources.loadImage("loading/icon.png");
        canvas.drawImage(icon, 200, h1 / 2 - 50);
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(210, h1 + 5, 480, h1 + h2 + 5),
                "不出来", 60f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.LEFT));
        var frame = surface.makeImageSnapshot();

        return InputImage.makePngOrGif(images.get(0), input -> {
            var s2 = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas2 = s2.getCanvas();
            canvas2.drawImage(frame, 0, 0);
            var imgSmall = ImageEdit.resizeWidth(input, imgWSmall);
            canvas2.drawImage(imgSmall, 100, h1 + 5 + (h2 - imgSmall.getHeight()) / 2);
            return s2.makeImageSnapshot();
        });
    }
}
