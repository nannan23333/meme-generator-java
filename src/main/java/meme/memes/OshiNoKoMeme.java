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

/** 移植自 memes/oshi_no_ko.rs。 */
@Component
public class OshiNoKoMeme extends AbstractMeme {

    public OshiNoKoMeme() {
        super(new MemeInfo("oshi_no_ko",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("name", "网友", null, null, meme.core.ParserFlags.none()))),
                List.of("我推的网友"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 1, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 23, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = stringOption(options, "name", "");
        if (name == null || name.isEmpty()) {
            throw meme.core.MemeError.feedback("选项 name 不能为空");
        }
        var nameImg = Text2Image.fromText(name, 150f, "HiraginoMin-W5-90-RKSJ-H-2.ttc",
                null, 0xFFFFFFFF, 8f);
        if (nameImg.longestLine() > 800f) {
            throw meme.core.MemeError.textOverLength(name);
        }
        int textH = Math.round((float) Math.ceil(nameImg.height()));
        var left = ImageEdit.resizeHeight(MemeResources.loadImage("oshi_no_ko/text1.png"), textH);
        var right = ImageEdit.resizeHeight(MemeResources.loadImage("oshi_no_ko/text2.png"), textH);
        int textW = left.getWidth() + Math.round(nameImg.longestLine()) + right.getWidth();
        var ts = meme.utils.TextRenderer.newSurface(textW, textH);
        var tc = ts.getCanvas();
        tc.drawImage(left, 0, 0);
        nameImg.drawOnCanvas(tc, left.getWidth(), 0);
        tc.drawImage(right, left.getWidth() + Math.round(nameImg.longestLine()), 0);
        var textImg = ImageEdit.resizeWidth(ts.makeImageSnapshot(), 663);

        var background = MemeResources.loadImage("oshi_no_ko/background.png");
        var foreground = MemeResources.loadImage("oshi_no_ko/foreground.png");
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(background.getWidth(), background.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(background, 0, 0);
            var img = ImageEdit.resizeFit(input, 681, 692, ImageEdit.Fit.COVER);
            canvas.drawImage(img, 0, 0);
            canvas.drawImage(textImg, 9, 102 - textImg.getHeight() / 2);
            canvas.drawImage(foreground, 0, 0);
            return surface.makeImageSnapshot();
        });
    }
}
