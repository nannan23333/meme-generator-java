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

/** 移植自 memes/always.rs。 */
@Component
public class AlwaysMeme extends AbstractMeme {

    public AlwaysMeme() {
        super(new MemeInfo("always",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("mode", "normal", List.of("normal", "circle", "loop"), null, meme.core.ParserFlags.none()), new MemeOption.BooleanOption("circle", false, null, meme.core.ParserFlags.none()), new MemeOption.BooleanOption("loop", false, null, meme.core.ParserFlags.none()))),
                List.of("一直"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2021, 12, 2, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 9, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String mode = stringOption(options, "mode", "normal");
        if (boolOption(options, "circle", false)) mode = "circle";
        if (boolOption(options, "loop", false)) mode = "loop";
        final String m = mode;
        if (m.equals("circle")) {
            return alwaysAlways(images, false);
        }
        if (m.equals("loop")) {
            return alwaysAlways(images, true);
        }
        return alwaysNormal(images);
    }

    private byte[] alwaysNormal(List<InputImage> images) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        float ratio = (float) input.getHeight() / input.getWidth();
        int imgBigW = 500;
        int imgBigH = Math.round(imgBigW * ratio);
        int imgSmallW = 100;
        int imgSmallH = Math.round(imgSmallW * ratio);
        int h1 = imgBigH;
        int h2 = Math.max(imgSmallH, 80);
        int frameW = imgBigW;
        int frameH = h1 + h2 + 10;
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(20, h1 + 5, 280, frameH - 5),
                "要我一直", 20f, 60f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.RIGHT));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(400, h1 + 5, 480, frameH - 5),
                "吗", 20f, 60f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.LEFT));
        return InputImage.makePngOrGif(images.get(0), inp -> {
            var s2 = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var c2 = s2.getCanvas();
            c2.drawImage(surface.makeImageSnapshot(), 0, 0);
            var imageBig = ImageEdit.resizeWidth(inp, imgBigW);
            var imageSmall = ImageEdit.resizeWidth(inp, imgSmallW);
            c2.drawImage(imageBig, 0, 0);
            c2.drawImage(imageSmall, 290, h1 + 5 + (h2 - imageSmall.getHeight()) / 2);
            return s2.makeImageSnapshot();
        });
    }

    private byte[] alwaysAlways(List<InputImage> images, boolean loop) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        float ratio = (float) input.getHeight() / input.getWidth();
        int imgBigW = 500;
        int imgBigH = Math.round(imgBigW * ratio);
        int imgSmallW = 100;
        int imgSmallH = Math.round(imgSmallW * ratio);
        int imgTinyW = 20;
        int imgTinyH = Math.round(imgTinyW * ratio);
        int textH = Math.max(imgSmallH + imgTinyH + 10, 80);
        int frameW = imgBigW;
        int frameH = imgBigH + textH;
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(20, imgBigH + 5, 280, frameH - 5),
                "要我一直", 20f, 60f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.RIGHT));
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(canvas,
                io.github.humbleui.types.Rect.makeLTRB(400, imgBigH + 5, 480, frameH - 5),
                "吗", 20f, 60f,
                meme.utils.TextRenderer.params().align(meme.utils.TextRenderer.Params.Align.LEFT));
        var textFrame = surface.makeImageSnapshot();

        int frameNum = 20;
        float coeff = (float) Math.pow(5.0, 1.0 / frameNum);

        java.util.function.BiFunction<Integer, io.github.humbleui.skija.Image, io.github.humbleui.skija.Image> func =
                (i, inp) -> {
                    var s1 = meme.utils.TextRenderer.newSurface(frameW, frameH);
                    var c1 = s1.getCanvas();
                    c1.drawImage(textFrame, 0, 0);
                    var image = ImageEdit.resizeWidth(inp, imgBigW);
                    c1.drawImage(image, 0, 0);
                    var baseFrame = s1.makeImageSnapshot();

                    var s2 = meme.utils.TextRenderer.newSurface(frameW, frameH);
                    var c2 = s2.getCanvas();
                    c2.clear(0xFFFFFFFF);
                    float r = (float) Math.pow(coeff, i);
                    for (int k = 0; k < 4; k++) {
                        int x = Math.round(358f * (1 - r));
                        int y = Math.round(frameH * (1 - r));
                        int w = Math.round(frameW * r);
                        int h = Math.round(frameH * r);
                        var piece = ImageEdit.resizeExact(baseFrame, Math.max(1, w), Math.max(1, h));
                        c2.drawImage(piece, x, y);
                        r /= 5f;
                    }
                    return s2.makeImageSnapshot();
                };

        if (!loop) {
            return ImageEdit.encodePng(func.apply(0, input));
        }
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < frameNum; i++) {
            frames.add(func.apply(i, input));
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
