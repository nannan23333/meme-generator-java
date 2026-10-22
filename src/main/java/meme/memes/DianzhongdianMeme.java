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

/** 移植自 memes/dianzhongdian.rs。 */
@Component
public class DianzhongdianMeme extends AbstractMeme {

    public DianzhongdianMeme() {
        super(new MemeInfo("dianzhongdian",
                new MemeParams(1, 1, 1, 2, List.of("救命啊"), List.of()),
                List.of("入典", "典中典", "黑白草图"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        String trans = texts.size() >= 2 ? texts.get(1) : null;
        var img0 = images.get(0).image();
        int imgW = 500;
        int imgH = imgW * img0.getHeight() / img0.getWidth();
        int frameH = trans != null ? imgH + 100 : imgH + 70;
        var surface = meme.utils.TextRenderer.newSurface(imgW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFF000000);
        var gray = ImageEdit.grayscale(ImageEdit.resizeWidth(img0, imgW));
        canvas.drawImage(gray, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(20, imgH, imgW - 20, imgH + 60),
                text, 25f, 50f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        if (trans != null) {
            meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                    canvas,
                    io.github.humbleui.types.Rect.makeLTRB(20, imgH + 60, imgW - 20, imgH + 90),
                    trans, 15f, 30f,
                    meme.utils.TextRenderer.params().fill(0xFFFFFFFF));
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
