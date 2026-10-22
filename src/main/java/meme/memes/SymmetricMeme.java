package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/symmetric.rs。 */
@Component
public class SymmetricMeme extends AbstractMeme {

    public SymmetricMeme() {
        super(new MemeInfo("symmetric",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("direction", "left", List.of("left", "right", "top", "bottom"), null, meme.core.ParserFlags.none()))),
                List.of("对称"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String dir = stringOption(options, "direction", "left");
        if (boolOption(options, "left", false)) dir = "left";
        if (boolOption(options, "right", false)) dir = "right";
        if (boolOption(options, "top", false)) dir = "top";
        if (boolOption(options, "bottom", false)) dir = "bottom";
        final String direction = dir;
        return InputImage.makePngOrGif(images.get(0), img -> {
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            int fw, fh;
            int[][] rects;
            int[][] poss;
            if (direction.equals("left") || direction.equals("right")) {
                fw = imgW / 2 * 2; fh = imgH;
                rects = new int[][]{{0, 0, imgW / 2, imgH}, {imgW / 2, 0, imgW / 2 * 2, imgH}};
                poss = new int[][]{{0, 0}, {imgW / 2, 0}};
                if (direction.equals("right")) {
                    rects = new int[][]{{imgW / 2, 0, imgW / 2 * 2, imgH}, {0, 0, imgW / 2, imgH}};
                }
            } else {
                fw = imgW; fh = imgH / 2 * 2;
                rects = new int[][]{{0, 0, imgW, imgH / 2}, {0, imgH / 2, imgW, imgH / 2 * 2}};
                poss = new int[][]{{0, 0}, {0, imgH / 2}};
                if (direction.equals("bottom")) {
                    rects = new int[][]{{0, imgH / 2, imgW, imgH / 2 * 2}, {0, 0, imgW, imgH / 2}};
                }
            }
            // 先整图翻转，再从翻转后的图里裁第二块（Rust 的 img.flip().crop(size2)）
            var part1 = ImageEdit.crop(img, rects[0][0], rects[0][1], rects[0][2], rects[0][3]);
            var flipped = (direction.equals("left") || direction.equals("right"))
                    ? ImageEdit.flipHorizontal(img) : ImageEdit.flipVertical(img);
            var part2 = ImageEdit.crop(flipped, rects[1][0], rects[1][1], rects[1][2], rects[1][3]);
            var surface = meme.utils.TextRenderer.newSurface(fw, fh);
            var canvas = surface.getCanvas();
            canvas.drawImage(part1, poss[0][0], poss[0][1]);
            canvas.drawImage(part2, poss[1][0], poss[1][1]);
            return surface.makeImageSnapshot();
        });
    }
}
