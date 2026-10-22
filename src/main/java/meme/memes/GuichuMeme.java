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

/** 移植自 memes/guichu.rs。 */
@Component
public class GuichuMeme extends AbstractMeme {

    public GuichuMeme() {
        super(new MemeInfo("guichu",
                new MemeParams(1, 1, 0, 0, List.of(), List.of(new MemeOption.StringOption("direction", "left", List.of("left", "right", "top", "bottom"), null, meme.core.ParserFlags.none()))),
                List.of("鬼畜"), List.of(), Set.of(),
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
        // 对齐 Rust：make_gif_or_combined_gif(32 帧 / 0.2s)，静态图也要出 GIF
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var img = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            boolean horizontal = direction.equals("left") || direction.equals("right");

            // 原图帧
            if (outIdx == 0 || outIdx == 1 || outIdx == 2 || outIdx == 6 || outIdx == 7
                    || outIdx == 8 || outIdx == 12 || outIdx == 13 || outIdx == 14
                    || outIdx == 18 || outIdx == 20 || outIdx == 22) {
                return img;
            }
            var flipped = horizontal ? ImageEdit.flipHorizontal(img) : ImageEdit.flipVertical(img);
            // 翻转帧
            if (outIdx == 3 || outIdx == 4 || outIdx == 5 || outIdx == 9 || outIdx == 10
                    || outIdx == 11 || outIdx == 15 || outIdx == 16 || outIdx == 17
                    || outIdx == 19 || outIdx == 21 || outIdx == 23) {
                return flipped;
            }
            // 对称帧
            int[][] rects;
            int[][] poss;
            switch (direction) {
                case "right" -> {
                    rects = new int[][]{{imgW / 2, 0, imgW / 2 * 2, imgH}, {0, 0, imgW / 2, imgH}};
                    poss = new int[][]{{imgW / 2, 0}, {0, 0}};
                }
                case "top" -> {
                    rects = new int[][]{{0, 0, imgW, imgH / 2}, {0, imgH / 2, imgW, imgH / 2 * 2}};
                    poss = new int[][]{{0, 0}, {0, imgH / 2}};
                }
                case "bottom" -> {
                    rects = new int[][]{{0, imgH / 2, imgW, imgH / 2 * 2}, {0, 0, imgW, imgH / 2}};
                    poss = new int[][]{{0, imgH / 2}, {0, 0}};
                }
                default -> {
                    rects = new int[][]{{0, 0, imgW / 2, imgH}, {imgW / 2, 0, imgW / 2 * 2, imgH}};
                    poss = new int[][]{{0, 0}, {imgW / 2, 0}};
                }
            }
            var part1 = ImageEdit.crop(img, rects[0][0], rects[0][1], rects[0][2], rects[0][3]);
            var part2raw = ImageEdit.crop(flipped, rects[1][0], rects[1][1], rects[1][2], rects[1][3]);
            var surface = meme.utils.TextRenderer.newSurface(imgW, imgH);
            var canvas = surface.getCanvas();
            canvas.drawImage(part1, poss[0][0], poss[0][1]);
            canvas.drawImage(part2raw, poss[1][0], poss[1][1]);
            var symmetric = surface.makeImageSnapshot();
            if (outIdx == 24 || outIdx == 25 || outIdx == 28 || outIdx == 29) {
                return symmetric;
            }
            var big = ImageEdit.resizeWidth(symmetric, imgW * 2);
            var s2 = meme.utils.TextRenderer.newSurface(imgW, imgH);
            s2.getCanvas().drawImage(big, -imgW / 2f, -imgH / 2f);
            return s2.makeImageSnapshot();
        }, 32, 0.2f);
    }
}
