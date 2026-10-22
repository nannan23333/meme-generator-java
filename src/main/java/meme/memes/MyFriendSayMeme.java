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

/** 移植自 memes/my_friend_say.rs。 */
@Component
public class MyFriendSayMeme extends AbstractMeme {

    public MyFriendSayMeme() {
        super(new MemeInfo("my_friend_say",
                new MemeParams(1, 1, 1, 10, List.of("让我康康"), List.of()),
                List.of("我朋友说"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 11, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        var nameImg = Text2Image.fromText(name, 25f, null, 0xFF868894, null, null);
        int nameW = Math.round(nameImg.longestLine());
        int nameH = Math.round(nameImg.height());
        if (nameW >= 600) {
            throw meme.core.MemeError.textOverLength(name);
        }
        var corner1 = MemeResources.loadImage("my_friend/corner1.png");
        var corner2 = MemeResources.loadImage("my_friend/corner2.png");
        var corner3 = MemeResources.loadImage("my_friend/corner3.png");
        var corner4 = MemeResources.loadImage("my_friend/corner4.png");
        var label = MemeResources.loadImage("my_friend/label.png");
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var avatar = ImageEdit.resizeExact(ImageEdit.circle(input), 100, 100);

        java.util.function.Function<String, io.github.humbleui.skija.Image> makeDialog = text -> {
            var textImg = Text2Image.fromText(text, 40f, null, null, null, null);
            textImg.layout(600f);
            int textW = Math.round(textImg.longestLine());
            int textH = Math.round(textImg.height());
            int boxW = Math.max(textW, nameW + 15) + 140;
            int boxH = Math.max(textH + 103, 150);
            var boxSurface = meme.utils.TextRenderer.newSurface(boxW, boxH);
            var bc = boxSurface.getCanvas();
            bc.drawImage(corner1, 0, 0);
            bc.drawImage(corner2, 0, boxH - 75);
            bc.drawImage(corner3, textW + 70, 0);
            bc.drawImage(corner4, textW + 70, boxH - 75);
            try (var white = new io.github.humbleui.skija.Paint().setColor(0xFFFFFFFF)) {
                bc.drawRect(io.github.humbleui.types.Rect.makeXYWH(70, 20, textW, boxH - 40), white);
                bc.drawRect(io.github.humbleui.types.Rect.makeXYWH(27, 75, textW + 88, boxH - 150), white);
            }
            textImg.drawOnCanvas(bc, 70, 20 + (boxH - 40 - textH) / 2f);
            var boxImg = boxSurface.makeImageSnapshot();

            var ds = meme.utils.TextRenderer.newSurface(boxImg.getWidth() + 130, boxImg.getHeight() + 60);
            var dc = ds.getCanvas();
            dc.clear(0xFFEAEDF4);
            dc.drawImage(avatar, 20, 20);
            dc.drawImage(boxImg, 130, 60);
            dc.drawImage(label, 160, 25);
            nameImg.drawOnCanvas(dc, 260, 22 + (35 - nameH) / 2f);
            return ds.makeImageSnapshot();
        };

        List<io.github.humbleui.skija.Image> dialogs = new ArrayList<>();
        for (String t : texts) {
            dialogs.add(makeDialog.apply(t));
        }
        int frameW = dialogs.stream().mapToInt(io.github.humbleui.skija.Image::getWidth).max().orElse(0);
        int frameH = dialogs.stream().mapToInt(io.github.humbleui.skija.Image::getHeight).sum();
        var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFEAEDF4);
        int currentH = 0;
        for (var dialog : dialogs) {
            canvas.drawImage(dialog, 0, currentH);
            currentH += dialog.getHeight();
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
