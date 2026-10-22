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

/** 移植自 memes/pjsk.rs。 */
@Component
public class PjskMeme extends AbstractMeme {

    public PjskMeme() {
        super(new MemeInfo("pjsk",
                new MemeParams(0, 0, 1, 1, List.of(), List.of(new MemeOption.StringOption("character", null, List.of("airi", "akito", "an", "emu", "ena", "haruka", "honami", "ichika", "kaito", "kanade", "kohane", "len", "luka", "mafuyu", "meiko", "miku", "minori", "mizuki", "nene", "rin", "rui", "saki", "shiho", "shizuku", "touya", "tsukasa"), null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("number", null, 1, 16, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("size", 50, 20, 100, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("rotate", null, -40, 40, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("x_offset", 0, -100, 100, null, meme.core.ParserFlags.none()), new MemeOption.IntegerOption("y_offset", 0, -50, 150, null, meme.core.ParserFlags.none()))),
                List.of("pjsk", "世界计划"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 12, 19, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2025, 1, 18, 0, 0), ZoneOffset.ofHours(8))));
    }

    private record Character(String nameCn, String nameEn, int color, int imgNum) {
    }

    private static final Character[] CHARACTERS = {
            new Character("爱莉", "airi", 0xFFFB8AAC, 15),
            new Character("彰人", "akito", 0xFFFF7722, 13),
            new Character("杏", "an", 0xFF00BADC, 13),
            new Character("梦", "emu", 0xFFFF66BB, 13),
            new Character("绘名", "ena", 0xFFB18F6C, 16),
            new Character("遥", "haruka", 0xFF6495F0, 13),
            new Character("穗波", "honami", 0xFFF86666, 15),
            new Character("一歌", "ichika", 0xFF33AAEE, 15),
            new Character("KAITO", "kaito", 0xFF3366CC, 13),
            new Character("奏", "kanade", 0xFFBB6688, 14),
            new Character("心羽", "kohane", 0xFFFF6699, 14),
            new Character("连", "len", 0xFFD3BD00, 14),
            new Character("流歌", "luka", 0xFFF88CA7, 13),
            new Character("真冬", "mafuyu", 0xFF7171AF, 14),
            new Character("MEIKO", "meiko", 0xFFE4485F, 13),
            new Character("初音未来", "miku", 0xFF33CCBB, 13),
            new Character("实乃理", "minori", 0xFFF39E7D, 14),
            new Character("瑞希", "mizuki", 0xFFCA8DB6, 14),
            new Character("宁宁", "nene", 0xFF19CD94, 13),
            new Character("铃", "rin", 0xFFE8A505, 13),
            new Character("类", "rui", 0xFFBB88EE, 16),
            new Character("咲希", "saki", 0xFFF5B303, 15),
            new Character("志步", "shiho", 0xFFA0C10B, 15),
            new Character("雫", "shizuku", 0xFF5CD0B9, 13),
            new Character("冬弥", "touya", 0xFF0077DD, 15),
            new Character("司", "tsukasa", 0xFFF09A04, 15)
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String text = texts.get(0);
        String nameEn = stringOption(options, "character", null);
        Character character;
        if (nameEn == null) {
            character = CHARACTERS[(int) (Math.random() * CHARACTERS.length)];
        } else {
            Character found = null;
            for (Character c : CHARACTERS) {
                if (c.nameEn().equals(nameEn)) {
                    found = c;
                    break;
                }
            }
            if (found == null) {
                throw meme.core.MemeError.feedback("角色 " + nameEn + " 不存在");
            }
            character = found;
        }
        int num = 1 + (int) (Math.random() * character.imgNum());
        Object v = options.get("number");
        if (v instanceof Number n) {
            num = n.intValue();
        }
        if (num < 1 || num > character.imgNum) {
            throw meme.core.MemeError.feedback(
                    "角色 " + character.nameCn() + " 的图片编号错误，请输入 1-" + character.imgNum());
        }
        var frame = MemeResources.loadImage(
                "pjsk/" + character.nameEn() + "/%02d.png".formatted(num));
        int fontSize = intOption(options, "size", 50);
        var textImg = Text2Image.fromText(text, fontSize, "SSFangTangTi.ttf",
                character.color(), 0xFFFFFFFF, fontSize * 0.24f);
        int textW = Math.round((float) Math.ceil(textImg.longestLine()));
        int textH = Math.round((float) Math.ceil(textImg.height()));
        Integer rotateOpt = null;
        Object r = options.get("rotate");
        if (r instanceof Number rn) rotateOpt = rn.intValue();
        float angle = rotateOpt != null ? rotateOpt
                : -40 + (float) (Math.random() * 81);
        int xOff = intOption(options, "x_offset", 0);
        int yOff = intOption(options, "y_offset", 0);
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        int centerX = frame.getWidth() / 2 + xOff;
        int centerY = frame.getHeight() / 5 + yOff;
        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(angle);
        textImg.drawOnCanvas(canvas, -textW / 2f, -textH / 2f);
        canvas.restore();
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
