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

/** 移植自 memes/batitle.rs。 */
@Component
public class BatitleMeme extends AbstractMeme {

    public BatitleMeme() {
        super(new MemeInfo("batitle",
                new MemeParams(0, 0, 2, 2, List.of("Blue", "Archive"), List.of()),
                List.of("蔚蓝档案标题", "batitle"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 10, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 11, 2, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        float fontsize = 168f;
        // 对齐 Rust：font_families = ["Ro GSan Serif Std", "Glow Sans SC"]
        String font = "RoGSanSrfStd-Bd.otf|GlowSansSC-Normal-Heavy.otf";
        float tilt = -0.4f;
        String colorBlue = "#128AFA";
        String colorGray = "#2B2B2B";
        String bbcode = "[color=" + colorBlue + "]" + texts.get(0) + "[/color]"
                + "[color=" + colorGray + "][stroke=#ffffff]" + texts.get(1) + "[/stroke][/color]";
        var textImg = Text2Image.fromBbcode(bbcode, fontsize, font, 0xFF000000, 0xFFFFFFFF, 20f);
        float textW0 = textImg.longestLine();
        // Rust 的 text_h 来自 Skia Paragraph（≈ ascent + descent），不是我们的 1.2*字号；
        // dw 和画布宽都按它算，否则整段文字会左偏十几像素
        var fm = textImg.resolvedFont().getMetrics();
        float textH = -fm.getAscent() + fm.getDescent();
        float dw = Math.abs(textH * tilt);
        float textW = textW0 + dw;

        var ts = meme.utils.TextRenderer.newSurface(Math.round(textW), Math.round(textH) + 8);
        var tcanvas = ts.getCanvas();
        // 横向 shear（x' = x + tilt*y），对齐 Rust 的 Matrix::from_affine
        tcanvas.concat(new io.github.humbleui.skija.Matrix33(new float[]{1f, tilt, dw, 0f, 1f, 0f, 0f, 0f, 1f}));
        // 我们的 drawOnCanvas 基线是 0.9*字号，Rust 走 Skia Paragraph（基线≈ascent），
        // 这里按真实 ascent 补齐，否则整行文字会偏高约 0.25*字号
        var probeFont = textImg.resolvedFont();
        float dy = -probeFont.getMetrics().getAscent() - fontsize * 0.9f;
        textImg.drawOnCanvas(tcanvas, 0, dy);
        var textImage = ts.makeImageSnapshot();

        int paddingX = 50;
        int frameW = textImage.getWidth() + paddingX * 2;
        int frameH = 450;
        int textY = 120;
        int logoY = 10;
        var leftProbe = Text2Image.fromText(texts.get(0), fontsize, font, null, null, null);
        int leftX = Math.round(leftProbe.longestLine());
        int logoX = paddingX + leftX - 100;
        var halo = MemeResources.loadImage("batitle/halo.png");
        var cross = MemeResources.loadImage("batitle/cross.png");
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFFFFFFF);
        canvas.drawImage(halo, logoX, logoY);
        canvas.drawImage(textImage, paddingX, textY);
        canvas.drawImage(cross, logoX, logoY);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
