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

/** 移植自 memes/jiji_king.rs。 */
@Component
public class JijiKingMeme extends AbstractMeme {

    public JijiKingMeme() {
        super(new MemeInfo("jiji_king",
                new MemeParams(1, 1, 0, 1, List.of("我是急急国王") , List.of(new MemeOption.StringOption("block_text", "急", null, null, meme.core.ParserFlags.none()))),
                List.of("急急国王"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.isEmpty() ? "急急急" : texts.get(0);
        String blockText = stringOption(options, "block_text", "急");
        var blockSurface = meme.utils.TextRenderer.newSurface(90, 90);
        var bcanvas = blockSurface.getCanvas();
        bcanvas.clear(0xFF000000);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                bcanvas,
                io.github.humbleui.types.Rect.makeXYWH(0, 0, 90, 90),
                blockText, 30f, 60f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF).fontFamilies("NotoSansSC-Bold.ttf"));
        var block = blockSurface.makeImageSnapshot();

        var frame = MemeResources.loadImage("jiji_king/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        for (int i = 0; i < 5; i++) {
            canvas.drawImage(block, 10 + 100 * i, 200);
        }
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(10, 300, frame.getWidth() - 10, 390),
                text, 30f, 100f,
                meme.utils.TextRenderer.params().fontFamilies("NotoSansSC-Bold.ttf"));
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 125, 125);
        canvas.drawImage(img, 237, 5);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
