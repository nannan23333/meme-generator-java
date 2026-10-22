package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/marriage.rs。 */
@Component
public class MarriageMeme extends AbstractMeme {

    public MarriageMeme() {
        super(new MemeInfo("marriage",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("结婚申请", "结婚登记"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 15, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 15, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var left = MemeResources.loadImage("marriage/0.png");
        var right = MemeResources.loadImage("marriage/1.png");
        // Rust 用 resize_bound（不补边，画布尺寸跟着图走）
        var img = ImageEdit.resizeBound(images.get(0).image(), 1500, 1080, ImageEdit.Fit.CONTAIN);
        var surface = meme.utils.TextRenderer.newSurface(img.getWidth(), img.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(img, 0, 0);
        canvas.drawImage(left, 0, 0);
        canvas.drawImage(right, img.getWidth() - right.getWidth(), 0);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
