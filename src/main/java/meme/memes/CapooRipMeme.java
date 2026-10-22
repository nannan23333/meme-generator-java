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

/** 移植自 memes/capoo_rip.rs。 */
@Component
public class CapooRipMeme extends AbstractMeme {

    public CapooRipMeme() {
        super(new MemeInfo("capoo_rip",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("咖波撕"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 17, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 28, 0, 0), ZoneOffset.ofHours(8))));
    }

    // params1: {px,py,x0,y0,x1,y1,x2,y2,x3,y3}
    private static final int[][] P1 = {
            {61, 196, 140, 68, 0, 59, 33, 0, 165, 8},
            {63, 196, 136, 68, 0, 59, 29, 0, 158, 13},
            {62, 195, 137, 72, 0, 58, 27, 0, 167, 11},
            {95, 152, 0, 8, 155, 0, 163, 107, 13, 112},
            {108, 129, 0, 6, 128, 0, 136, 113, 10, 117},
            {84, 160, 0, 6, 184, 0, 190, 90, 10, 97}
    };
    // params2 项 0：左半 {px,py,4点}
    private static final int[][] P2A = {
            {78, 158, 0, 3, 86, 0, 97, 106, 16, 106},
            {89, 156, 0, 0, 80, 0, 94, 100, 14, 100}
    };
    // params2 项 1：右半
    private static final int[][] P2B = {
            {195, 156, 0, 4, 82, 0, 85, 106, 15, 110},
            {192, 151, 0, 7, 79, 3, 82, 107, 11, 112}
    };
    private static final int[] INDEXES = {0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5, 6, 7, 7};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var image = ImageEdit.resizeFit(input, 150, 100, ImageEdit.Fit.COVER);
        var imageLeft = ImageEdit.crop(image, 0, 0, 75, 100);
        var imageRight = ImageEdit.crop(image, 75, 0, 150, 100);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int index = INDEXES[i];
            var frame = MemeResources.loadImage("capoo_rip/%d.png".formatted(index));
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            if (index < 6) {
                int[] p = P1[index];
                var img = ImageEdit.perspective(image, p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                canvas.drawImage(img, p[0], p[1]);
            } else {
                int[] pa = P2A[index - 6];
                int[] pb = P2B[index - 6];
                var left = ImageEdit.perspective(imageLeft, pa[2], pa[3], pa[4], pa[5], pa[6], pa[7], pa[8], pa[9]);
                var right = ImageEdit.perspective(imageRight, pb[2], pb[3], pb[4], pb[5], pb[6], pb[7], pb[8], pb[9]);
                canvas.drawImage(left, pa[0], pa[1]);
                canvas.drawImage(right, pb[0], pb[1]);
            }
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.1f);
    }
}
