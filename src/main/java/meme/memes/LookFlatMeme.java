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

/** 移植自 memes/look_flat.rs。 */
@Component
public class LookFlatMeme extends AbstractMeme {

    public LookFlatMeme() {
        super(new MemeInfo("look_flat",
                new MemeParams(1, 1, 0, 1, List.of("可恶...被人看扁了") , List.of(new MemeOption.FloatOption("ratio", 2.0f, 1.0f, 10.0f, null, meme.core.ParserFlags.none()))),
                List.of("看扁"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 23, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "可恶...被人看扁了" : texts.get(0);
        float ratioTmp = 2f;
        Object v = options.get("ratio");
        if (v instanceof Number n) {
            ratioTmp = Math.max(1f, Math.min(10f, n.floatValue()));
        }
        final float ratio = ratioTmp;
        int imgW = 500;
        int textH = 80;
        var textSurface = meme.utils.TextRenderer.newSurface(imgW, textH);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(10, 0, imgW - 10, textH),
                text, 30f, 55f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var textImage = textSurface.makeImageSnapshot();
        // 对齐 Rust：画布 (500, 压扁后高度 + 80)，图在上、字在下，不做额外留白
        return InputImage.makePngOrGif(images.get(0), input -> {
            var img = ImageEdit.resizeWidth(input, imgW);
            int flatH = Math.max(1, (int) (img.getHeight() / ratio));
            var flat = ImageEdit.resizeExact(img, imgW, flatH);
            var surface = meme.utils.TextRenderer.newSurface(imgW, flatH + textH);
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            canvas.drawImage(flat, 0, 0);
            canvas.drawImage(textImage, 0, flatH);
            return surface.makeImageSnapshot();
        });
    }
}
