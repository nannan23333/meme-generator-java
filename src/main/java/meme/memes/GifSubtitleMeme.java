package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import meme.utils.Text2Image;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GIF 字幕基类：对齐 Rust gif_subtitle 宏——解码模板 GIF，按帧区间贴底部居中字幕，
 * 保留原帧时长。
 */
public abstract class GifSubtitleMeme extends AbstractMeme {

    private final String templateName;
    private final float fontSize;
    private final int[][] pieces;

    protected GifSubtitleMeme(String key, List<String> keywords, int[][] pieces,
                              List<String> defaultTexts, float fontSize,
                              String created, String modified) {
        super(new MemeInfo(key,
                new MemeParams(0, 0, pieces.length, pieces.length, defaultTexts, List.of()),
                keywords, List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(parseY(created), parseM(created), parseD(created), 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(parseY(modified), parseM(modified), parseD(modified), 0, 0), ZoneOffset.ofHours(8))));
        this.templateName = key;
        this.fontSize = fontSize;
        this.pieces = pieces;
    }

    private static int parseY(String d) {
        return Integer.parseInt(d.split("-")[0]);
    }

    private static int parseM(String d) {
        return Integer.parseInt(d.split("-")[1]);
    }

    private static int parseD(String d) {
        return Integer.parseInt(d.split("-")[2]);
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        byte[] gifData = MemeResources.readResource("images/gif_subtitle/" + templateName + ".gif");
        var frames = GifSupport.readGif(gifData);
        List<java.awt.image.BufferedImage> out = new ArrayList<>();
        int[] delays = new int[frames.size()];
        for (int i = 0; i < frames.size(); i++) {
            var frame = frames.get(i);
            delays[i] = frame.delayMs();
            var img = frame.image();
            // 找到覆盖当前帧的字幕段
            String subtitle = null;
            for (int p = 0; p < pieces.length; p++) {
                if (i >= pieces[p][0] && i < pieces[p][1]) {
                    subtitle = texts.get(p);
                    break;
                }
            }
            java.awt.image.BufferedImage result = img;
            if (subtitle != null) {
                var skImg = ImageEdit.fromBufferedImage(img);
                var surface = meme.utils.TextRenderer.newSurface(skImg.getWidth(), skImg.getHeight());
                var canvas = surface.getCanvas();
                canvas.drawImage(skImg, 0, 0);
                var textImg = Text2Image.fromText(subtitle, fontSize, null,
                        0xFFFFFFFF, 0xFF000000, fontSize / 10f);
                float paddingY = 5f;
                textImg.drawOnCanvas(canvas,
                        (skImg.getWidth() - textImg.longestLine()) / 2f,
                        skImg.getHeight() - paddingY - textImg.height());
                result = ImageEdit.toBufferedImage(surface.makeImageSnapshot());
            }
            out.add(result);
        }
        return GifSupport.writeGif(out, delays);
    }
}
