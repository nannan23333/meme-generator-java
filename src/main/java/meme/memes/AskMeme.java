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

/** 移植自 memes/ask.rs。 */
@Component
public class AskMeme extends AbstractMeme {

    public AskMeme() {
        super(new MemeInfo("ask",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("gender", "unknown", List.of("male", "female", "unknown"), null, meme.core.ParserFlags.none()))),
                List.of("问问"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 2, 23, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        String gender = stringOption(options, "gender", "female");
        if (boolOption(options, "male", false)) gender = "male";
        if (boolOption(options, "female", false)) gender = "female";
        String ta = gender.equals("male") ? "他" : "她";
        String text = name + "不知道哦。";

        var nameImage = Text2Image.fromText(name, 40f, "HYWenHei-85W.ttf",
                0xFFFCBD0D, 0xB3000000, 3f);
        var textImage = Text2Image.fromText(text, 38f, "HYWenHei-85W.ttf",
                0xFFFFFFFF, 0xB3000000, 3f);
        if (nameImage.longestLine() > 500f) {
            throw meme.core.MemeError.textOverLength(name);
        }
        float lineW = textImage.longestLine() + 200f;
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        int imgH1 = 900;
        // 对齐 Rust：img_w = max(w*900/h, line_w+100).min(900)
        int rawW = input.getWidth() * imgH1 / input.getHeight();
        int imgW1 = Math.min(Math.max(rawW, (int) (lineW + 100f)), imgH1);
        var s1 = meme.utils.TextRenderer.newSurface(imgW1, imgH1);
        var c1 = s1.getCanvas();
        var mask = ImageEdit.resizeFit(MemeResources.loadImage("ask/mask.png"), imgW1, imgH1, ImageEdit.Fit.COVER);
        c1.drawImage(mask, 0, 0);
        var line = ImageEdit.resizeExact(MemeResources.loadImage("ask/line.png"),
                Math.round(lineW), MemeResources.loadImage("ask/line.png").getHeight());
        c1.drawImage(line, (imgW1 - Math.round(lineW)) / 2, 725);
        nameImage.drawOnCanvas(c1, (imgW1 - nameImage.longestLine()) / 2f, 678);
        textImage.drawOnCanvas(c1, (imgW1 - textImage.longestLine()) / 2f, 740);
        var dialog0 = s1.makeImageSnapshot();
        int imgH2 = 640;
        var dialog = ImageEdit.resizeHeight(dialog0, imgH2);
        int imgW2 = dialog.getWidth();

        int paddingW = 30;
        int paddingH = 80;
        int frameWidth = imgW2 + paddingW * 2;
        int frameHeight = imgH2 + paddingH * 2;
        var surface = meme.utils.TextRenderer.newSurface(frameWidth, frameHeight);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(paddingW, 0, frameWidth - paddingW, paddingH),
                "让" + name + "告诉你吧", 35f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.LEFT));
        meme.utils.TextRenderer.drawTextArea(canvas,
                io.github.humbleui.types.Rect.makeLTRB(paddingW, frameHeight - paddingH,
                        frameWidth - paddingW, frameHeight),
                "啊这，" + ta + "说不知道", 35f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.LEFT));
        return InputImage.makePngOrGif(images.get(0), inp -> {
            var s2 = meme.utils.TextRenderer.newSurface(frameWidth, frameHeight);
            var c2 = s2.getCanvas();
            c2.drawImage(surface.makeImageSnapshot(), 0, 0);
            var image = ImageEdit.resizeFit(inp, imgW2, imgH2, ImageEdit.Fit.COVER);
            c2.drawImage(image, paddingW, paddingH);
            c2.drawImage(dialog, paddingW, paddingH);
            return s2.makeImageSnapshot();
        });
    }
}
