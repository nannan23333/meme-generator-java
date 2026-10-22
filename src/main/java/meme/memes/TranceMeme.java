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

/** 移植自 memes/trance.rs。 */
@Component
public class TranceMeme extends AbstractMeme {

    public TranceMeme() {
        super(new MemeInfo("trance",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("恍惚"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 12, 11, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return InputImage.makePngOrGif(images.get(0), img -> {
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            int dh = (int) (imgH * 0.1f);
            int frameW = imgW;
            int frameH = imgH + dh;
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.clear(0x00000000);
            // 对齐 Rust：主图下移 dh，再用极低透明度（0.01）在自己上下各糊一层
            canvas.drawImage(img, 0, dh);
            var ghost = ImageEdit.transparency(img, 0.01f);
            for (int i = dh - 1; i >= 0; i--) {
                canvas.drawImage(ghost, 0, i);
            }
            for (int i = dh; i < dh * 2; i++) {
                canvas.drawImage(ghost, 0, i);
            }
            var frame = surface.makeImageSnapshot();
            return ImageEdit.crop(frame, 0, dh, frameW, frameH);
        });
    }
}
