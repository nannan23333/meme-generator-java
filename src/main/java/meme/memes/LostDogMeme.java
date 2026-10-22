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

/** 移植自 memes/lost_dog.rs。 */
@Component
public class LostDogMeme extends AbstractMeme {

    public LostDogMeme() {
        super(new MemeInfo("lost_dog",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("寻狗启事"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 19, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 1, 20, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int k = 2;
        int wBig = 663 * k;
        int w = 540 * k;
        int h = 540 * k;
        float r = 466 * k;
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeFit(input, wBig, h, ImageEdit.Fit.COVER);
        // 对齐 Rust SkSL：theta = asin(|dx|/r)；x_ = w_/2 + dx/cos(theta)
        var mapped = ImageEdit.remap(img, (x, y) -> {
            float dx = x - w / 2f;
            float dy = y - h / 2f;
            float theta = (float) Math.asin(Math.min(1.0, Math.abs(dx) / r));
            float xs = wBig / 2f + (float) (dx / Math.cos(theta));
            float ys = h / 2f + (float) (dy / Math.cos(theta));
            return new float[]{xs, ys};
        });
        var surface = meme.utils.TextRenderer.newSurface(w, h);
        surface.getCanvas().drawImage(mapped, 0, 0);
        var shrunk = ImageEdit.resizeExact(surface.makeImageSnapshot(), w / k, h / k);
        var frame = MemeResources.loadImage("lost_dog/0.png");
        var surface2 = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface2.getCanvas();
        canvas.clear(0xFFFFFFFF);
        canvas.drawImage(shrunk, 295, 165);
        canvas.drawImage(frame, 0, 0);
        return ImageEdit.encodePng(surface2.makeImageSnapshot());
    }
}
