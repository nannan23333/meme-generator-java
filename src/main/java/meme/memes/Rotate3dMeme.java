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

/** 移植自 memes/rotate_3d.rs。 */
@Component
public class Rotate3dMeme extends AbstractMeme {

    public Rotate3dMeme() {
        super(new MemeInfo("rotate_3d",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("三维旋转"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2024, 4, 30, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2024, 4, 30, 0, 0), ZoneOffset.ofHours(8))));
    }

    /** 对齐 Rust rotate_y：Y 轴旋转 + 透视投影，返回四点透视目标。 */
    private static float[][] rotateYPoints(int imgW, int imgH, float theta, float z) {
        float pcx = imgW / 2f, pcy = imgH / 2f;
        float[][] orgs = {
                {0, 0}, {imgW, 0}, {imgW, imgH}, {0, imgH}
        };
        float cos = (float) Math.cos(theta);
        float sin = (float) Math.sin(theta);
        float[][] pts = new float[4][2];
        for (int i = 0; i < 4; i++) {
            float ox = orgs[i][0] - pcx;
            float oy = orgs[i][1] - pcy;
            float dx = ox * cos;            // org.z = 0
            float dz = -ox * sin;
            pts[i][0] = dx * z / (z - dz) + pcx;
            pts[i][1] = oy * z / (z - dz) + pcy;
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
        return pts;
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img0 = images.get(0).image();
        int imgW = img0.getWidth();
        int imgH = img0.getHeight();
        float fov = (float) Math.toRadians(45);
        float z = (float) Math.hypot(imgW, imgH) / (float) (2.0 * Math.tan(fov / 2));
        int frameW = Math.round(imgW * 1.2f);
        int frameH = Math.round(imgH * 1.5f);
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            float theta = outIdx * (float) Math.toRadians(12);
            var rotated = ImageEdit.perspective(input, rotateYPoints(imgW, imgH, theta, z));
            var surface = meme.utils.TextRenderer.newSurface(frameW, frameH);
            var canvas = surface.getCanvas();
            canvas.drawImage(rotated, (frameW - rotated.getWidth()) / 2,
                    (frameH - rotated.getHeight()) / 2);
            return surface.makeImageSnapshot();
        }, 30, 0.07f);
    }
}
