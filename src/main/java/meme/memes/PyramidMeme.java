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

/** 移植自 memes/pyramid.rs。 */
@Component
public class PyramidMeme extends AbstractMeme {

    public PyramidMeme() {
        super(new MemeInfo("pyramid",
                new MemeParams(1, 4, 0, 0, List.of(), List.of()),
                List.of("四棱锥", "金字塔"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 16, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 8, 18, 0, 0), ZoneOffset.ofHours(8))));
    }

    /** 对齐 Rust crop_to_triangle：三角形裁剪。 */
    private static io.github.humbleui.skija.Image cropToTriangle(io.github.humbleui.skija.Image img) {
        int w = img.getWidth();
        int h = img.getHeight();
        try (var surface = meme.utils.TextRenderer.newSurface(w, h);
             var path = io.github.humbleui.skija.Path.makePolygon(new io.github.humbleui.types.Point[]{
                     new io.github.humbleui.types.Point(w / 2f, 0f),
                     new io.github.humbleui.types.Point(0f, (float) h),
                     new io.github.humbleui.types.Point((float) w, (float) h)}, true)) {
            var canvas = surface.getCanvas();
            canvas.save();
            canvas.clipPath(path);
            canvas.drawImage(img, 0, 0);
            canvas.restore();
            return surface.makeImageSnapshot();
        }
    }

    /** 对齐 Rust pyramid 的 rotate_y：梯形面（前高后低）透视。 */
    private static Object[] pyramidFace(io.github.humbleui.skija.Image img, float thetaRad,
                                        float z, float a, float h, float rh) {
        float[][] orgs = {
                {-a / 2, -h / 2, 0}, {a / 2, -h / 2, 0}, {a / 2, h / 2, a / 2}, {-a / 2, h / 2, a / 2}
        };
        float cos = (float) Math.cos(thetaRad);
        float sin = (float) Math.sin(thetaRad);
        float[][] pts = new float[4][2];
        for (int i = 0; i < 4; i++) {
            float dx = orgs[i][0] * cos + orgs[i][2] * sin;
            float dy = orgs[i][1];
            float dz = -orgs[i][0] * sin + orgs[i][2] * cos;
            pts[i][0] = dx * z / (z - dz);
            pts[i][1] = dy * z / (z - dz);
        }
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        for (float[] p : pts) {
            minX = Math.min(minX, p[0]);
            minY = Math.min(minY, p[1]);
        }
        for (float[] p : pts) {
            p[0] -= minX;
            p[1] -= minY;
        }
        var sized = ImageEdit.resizeFit(img, (int) a, (int) rh, ImageEdit.Fit.COVER);
        var tri = cropToTriangle(sized);
        var persp = ImageEdit.perspective(tri, pts);
        return new Object[]{150f + minX, 150f + minY, persp};
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int frameNumPerImage = 15;
        int frameNum = images.size() * frameNumPerImage;
        float thetaStep = 90f / frameNumPerImage;
        float fov = (float) (45.0 * Math.PI / 180);
        float z = (float) (Math.hypot(300, 300) / (2.0 * Math.tan(fov / 2)));
        float a = 180f, h = 180f, rh = (float) Math.hypot(a, h);

        List<io.github.humbleui.skija.Image> inputs = new ArrayList<>();
        for (InputImage ii : images) {
            inputs.add(ii.isMultiFrame()
                    ? ImageEdit.fromBufferedImage(ii.gifFrames().get(0).image())
                    : ii.image());
        }
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < frameNum; i++) {
            int idx1 = i / frameNumPerImage;
            int idx2 = (idx1 + 1) % inputs.size();
            float theta1 = (i % frameNumPerImage) * thetaStep;
            float theta2 = theta1 - 90f;
            var surface = meme.utils.TextRenderer.newSurface(300, 300);
            var canvas = surface.getCanvas();
            var r2 = pyramidFace(inputs.get(idx2), (float) Math.toRadians(theta2), z, a, h, rh);
            canvas.drawImage((io.github.humbleui.skija.Image) r2[2], (float) r2[0], (float) r2[1]);
            var r1 = pyramidFace(inputs.get(idx1), (float) Math.toRadians(theta1), z, a, h, rh);
            canvas.drawImage((io.github.humbleui.skija.Image) r1[2], (float) r1[0], (float) r1[1]);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.06f);
    }
}
