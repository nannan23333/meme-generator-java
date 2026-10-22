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

/** 移植自 memes/dont_touch.rs。 */
@Component
public class DontTouchMeme extends AbstractMeme {

    public DontTouchMeme() {
        super(new MemeInfo("dont_touch",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("别碰"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 27, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 4, 27, 0, 0), ZoneOffset.ofHours(8))));
    }

    private java.util.List<Integer> dominantColors(io.github.humbleui.skija.Image img) {
        var bi = ImageEdit.toBufferedImage(img);
        int w = bi.getWidth();
        int h = bi.getHeight();
        if (w > 200 || h > 200) {
            int nw = Math.min(200, w);
            int nh = Math.min(200, h);
            bi = ImageEdit.toBufferedImage(ImageEdit.resizeFit(img, nw, nh, ImageEdit.Fit.CONTAIN));
            w = bi.getWidth();
            h = bi.getHeight();
        }
        double[][][][] buckets = new double[2][2][2][];
        for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++) for (int k = 0; k < 2; k++)
            buckets[i][j][k] = new double[]{0, 0, 0, 0}; // r, g, b, count
        double total = 0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = bi.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                double[] bk = buckets[r >> 7][g >> 7][b >> 7];
                bk[0] += r; bk[1] += g; bk[2] += b; bk[3] += 1;
                total += 1;
            }
        }
        java.util.List<double[]> avgs = new ArrayList<>();
        for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++) for (int k = 0; k < 2; k++) {
            double[] bk = buckets[i][j][k];
            if (bk[3] > 0) {
                avgs.add(new double[]{bk[0] / bk[3], bk[1] / bk[3], bk[2] / bk[3], bk[3]});
            }
        }
        avgs.sort((a, b2) -> Double.compare(b2[3], a[3]));
        java.util.List<Integer> colors = new ArrayList<>();
        for (double[] a : avgs) {
            if (a[3] / total > 0.01) {
                colors.add(0xFF000000 | ((int) Math.round(a[0]) << 16)
                        | ((int) Math.round(a[1]) << 8) | (int) Math.round(a[2]));
            }
        }
        return colors;
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("dont_touch/0.png");
        var mask = MemeResources.loadImage("dont_touch/mask.png");
        var maskBi = ImageEdit.toBufferedImage(mask);
        var input = images.get(0).isMultiFrame()
                ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(0).image())
                : images.get(0).image();
        var img = ImageEdit.resizeFit(input, 250, 250, ImageEdit.Fit.CONTAIN);
        var colors = dominantColors(img);
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        // 随机色块
        java.util.List<int[]> blockLocs = new ArrayList<>();
        int x1 = 200, y1 = 300, x2 = 400, y2 = 650;
        for (int n = 0; n < 150; n++) {
            int x = x1 + (int) (Math.random() * (x2 - x1 + 1));
            int y = y1 + (int) (Math.random() * (y2 - y1 + 1));
            if (x < maskBi.getWidth() && y < maskBi.getHeight() && (maskBi.getRGB(x, y) & 0xFF000000) == 0xFF000000) {
                continue;
            }
            boolean near = false;
            for (int[] bl : blockLocs) {
                if (Math.abs(x - bl[0]) < 13 && Math.abs(y - bl[1]) < 13) {
                    near = true;
                    break;
                }
            }
            if (near) continue;
            blockLocs.add(new int[]{x, y});
            int color = colors.get((int) (Math.random() * colors.size()));
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(45f);
            try (var paint = new io.github.humbleui.skija.Paint().setColor(color)) {
                canvas.drawRect(io.github.humbleui.types.Rect.makeXYWH(0, 0, 10, 10), paint);
            }
            canvas.restore();
        }
        canvas.drawImage(img, 25, 460);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
