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

/** 移植自 memes/repeat.rs。 */
@Component
public class RepeatMeme extends AbstractMeme {

    public RepeatMeme() {
        super(new MemeInfo("repeat",
                new MemeParams(1, 5, 1, 1, List.of("救命啊"), List.of()),
                List.of("复读"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 6, 8, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        var textImg = Text2Image.fromText(text, 50f, null, null, null, null);
        if (textImg.longestLine() > 900f) {
            throw meme.core.MemeError.textOverLength(text);
        }
        String time = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        var timeImg = Text2Image.fromText(time, 40f, null, 0xFF808080, null, null);

        java.util.function.BiFunction<io.github.humbleui.skija.Image, String, io.github.humbleui.skija.Image> singleMsg =
                (img, name) -> {
                    var userImg = ImageEdit.resizeExact(ImageEdit.circle(img), 100, 100);
                    var nameImg = Text2Image.fromText(name, 40f, null, null, null, null);
                    var surface = meme.utils.TextRenderer.newSurface(1079, 200);
                    var canvas = surface.getCanvas();
                    canvas.clear(0xFFF8F9FB);
                    canvas.drawImage(userImg, 50, 50);
                    nameImg.drawOnCanvas(canvas, 175, 45);
                    timeImg.drawOnCanvas(canvas, 200 + Math.round(nameImg.longestLine()), 50);
                    textImg.drawOnCanvas(canvas, 175, 100);
                    return surface.makeImageSnapshot();
                };

        List<io.github.humbleui.skija.Image> inputs = new ArrayList<>();
        for (InputImage ii : images) {
            inputs.add(ii.isMultiFrame()
                    ? ImageEdit.fromBufferedImage(ii.gifFrames().get(0).image())
                    : ii.image());
        }
        var ms = meme.utils.TextRenderer.newSurface(1079, 1000);
        var mc = ms.getCanvas();
        for (int i = 0; i < 5; i++) {
            var img = inputs.get(i % inputs.size());
            var msg = singleMsg.apply(img, images.get(i % images.size()).name());
            mc.drawImage(msg, 0, 200 * i);
        }
        var msgImg = ms.makeImageSnapshot();

        var ds = meme.utils.TextRenderer.newSurface(msgImg.getWidth(), msgImg.getHeight() * 2);
        var dc = ds.getCanvas();
        dc.drawImage(msgImg, 0, 0);
        dc.drawImage(msgImg, 0, msgImg.getHeight());
        var msgTwice = ds.makeImageSnapshot();

        var inputImg = MemeResources.loadImage("repeat/0.jpg");
        var selfImg = ImageEdit.resizeExact(ImageEdit.circle(inputs.get(0)), 75, 75);
        var is = meme.utils.TextRenderer.newSurface(inputImg.getWidth(), inputImg.getHeight());
        var ic = is.getCanvas();
        ic.drawImage(inputImg, 0, 0);
        ic.drawImage(selfImg, 15, 40);
        var inputImg2 = is.makeImageSnapshot();

        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            var surface = meme.utils.TextRenderer.newSurface(1079, 1192);
            var canvas = surface.getCanvas();
            canvas.drawImage(msgTwice, 0, -20 * i);
            canvas.drawImage(inputImg2, 0, 1000);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
