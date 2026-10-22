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

/** 移植自 memes/steam_message.rs。 */
@Component
public class SteamMessageMeme extends AbstractMeme {

    public SteamMessageMeme() {
        super(new MemeInfo("steam_message",
                new MemeParams(1, 1, 1, 1, List.of("黑神话：悟空"), List.of()),
                List.of("steam消息"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 21, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        if (name == null || name.isEmpty()) {
            name = "好友";
        }
        String game = texts.get(0);
        var textName = Text2Image.fromText(name, 65f, null, 0xFFE3FFC2, null, null);
        var textPlay = Text2Image.fromText("正在玩", 62f, null, 0xFFD1D1C0, null, null);
        var textGame = Text2Image.fromText(game, 65f, null, 0xFF91C257, null, null);

        int avatarW = 280;
        int paddingH = 50;
        int paddingV = 80;
        int marginRec = 6;
        int recW = 15;
        int marginText = 80;
        int textW = Math.round(Math.max(Math.max(Math.max(
                textName.longestLine(), textPlay.longestLine()), textGame.longestLine()), 1300f));
        int textX = paddingH + avatarW + marginRec + recW + marginText;
        int frameW = textX + textW + paddingH;
        int frameH = paddingV * 2 + avatarW;

        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFF14161F);
        try (var p = new io.github.humbleui.skija.Paint()) {
            p.setColor(0xFF191B23);
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(paddingH, paddingV, avatarW, avatarW), p);
            p.setColor(0xFF6CBE48);
            canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(paddingH + avatarW + marginRec, paddingV,
                    recW, frameH - paddingV * 2), p);
        }
        var logo = MemeResources.loadImage("steam_message/logo.png");
        canvas.drawImage(logo, frameW - 870, -370);
        textPlay.drawOnCanvas(canvas, textX, (frameH - textPlay.height()) / 2f);
        textName.drawOnCanvas(canvas, textX, paddingV + 40f - textName.height() / 2f);
        textGame.drawOnCanvas(canvas, textX, frameH - paddingV - 40f - textGame.height() / 2f);
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var avatar = ImageEdit.resizeFit(input, avatarW, avatarW, ImageEdit.Fit.COVER);
        canvas.drawImage(avatar, paddingH, paddingV);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
