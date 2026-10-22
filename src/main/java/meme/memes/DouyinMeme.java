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

/** 移植自 memes/douyin.rs。 */
@Component
public class DouyinMeme extends AbstractMeme {

    public DouyinMeme() {
        super(new MemeInfo("douyin",
                new MemeParams(0, 0, 1, 1, List.of("douyin"), List.of()),
                List.of("douyin"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 29, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        float fontsize = 200f;
        int offset = (int) (fontsize * 0.05f);
        int paddingX = 70;
        int paddingY = 30;
        int bgColor = 0xFF1C0B1B;
        var textRight = Text2Image.fromText(text, fontsize, null,
                0xFFFF0050, 0xFFFF0050, 10f);
        var textLeft = Text2Image.fromText(text, fontsize, null,
                0xFF00F5EB, 0xFF00F5EB, 10f);
        var textMid = Text2Image.fromText(text, fontsize, null,
                0xFFFFFFFF, 0xFFFFFFFF, 10f);

        int frameW = Math.round((float) Math.ceil(textMid.longestLine())) + paddingX * 2 + offset * 2;
        int frameH = Math.round((float) Math.ceil(textMid.height())) + paddingY * 2 + offset * 2;
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(bgColor);
        textRight.drawOnCanvas(canvas, paddingX + offset * 2f, paddingY + offset * 2f);
        textLeft.drawOnCanvas(canvas, paddingX, paddingY);
        textMid.drawOnCanvas(canvas, paddingX + offset, paddingY + offset);
        var frame = surface.makeImageSnapshot();

        int width = frameW - paddingX * 2;
        int height = frameH - paddingY * 2;
        int frameNum = 10;
        int devideNum = 6;
        float seed = 20f * 0.05f;
        float tilt = 0.17f;
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int f = 0; f < frameNum; f++) {
            var fs = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var fc = fs.getCanvas();
            fc.drawImage(frame, 0, 0);
            int lastYn = 0;
            int lastH = 0;
            int direction = 1;
            for (int i = 0; i < devideNum; i++) {
                int yn = lastYn + lastH;
                int h = Math.max(2, Math.round(height * (float) Math.random()));
                lastYn = yn;
                lastH = h;
                direction = -direction;
                if (yn + h > height) h = Math.max(2, height - yn);
                if (yn >= height) break;
                var piece = ImageEdit.crop(frame, paddingX, paddingY + yn,
                        paddingX + width, Math.min(paddingY + yn + h, frameH));
                fc.drawImage(piece, paddingX + i * direction * (int) seed, paddingY + yn);
            }
            var shifted = fs.makeImageSnapshot();
            int moveX = Math.round(frameH * tilt);
            var persp = ImageEdit.perspective(shifted,
                    moveX, 0, frameW + moveX, 0, frameW, frameH, 0, frameH);
            var fs2 = meme.utils.TextRenderer.newSurface(persp.getWidth(), persp.getHeight());
            var fc2 = fs2.getCanvas();
            fc2.clear(bgColor);
            fc2.drawImage(persp, 0, 0);
            frames.add(fs2.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.2f);
    }
}
