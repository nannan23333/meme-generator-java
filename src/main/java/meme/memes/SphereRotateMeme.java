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

/** 移植自 memes/sphere_rotate.rs。 */
@Component
public class SphereRotateMeme extends AbstractMeme {

    public SphereRotateMeme() {
        super(new MemeInfo("sphere_rotate",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("球面旋转"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        int totalFrames = 60;
        int canvasW = 300;
        int canvasH = 300;
        double radius = 1.2;
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var input = images.get(0).isMultiFrame()
                    ? ImageEdit.fromBufferedImage(images.get(0).gifFrames().get(inIdx).image())
                    : images.get(0).image();
            var bi = ImageEdit.toBufferedImage(input);
            int imgW = bi.getWidth();
            int imgH = bi.getHeight();
            double angle = outIdx / (double) totalFrames * Math.PI * 2;
            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);
            var dst = new java.awt.image.BufferedImage(canvasW, canvasH, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            for (int py = 0; py < canvasH; py++) {
                for (int px = 0; px < canvasW; px++) {
                    // 标准化坐标
                    double ux = (2.0 * px - canvasW) / canvasH;
                    double uy = (2.0 * py - canvasH) / canvasH;
                    // 相机 + Y 轴旋转（rotate_y: x' = c*x + s*z, z' = -s*x + c*z）
                    double rox0 = 0, roy0 = 0, roz0 = 3.5;
                    double rdx0 = ux, rdy0 = uy, rdz0 = -2.0;
                    double rox = cosA * rox0 + sinA * roz0;
                    double roy = roy0;
                    double roz = -sinA * rox0 + cosA * roz0;
                    double rdx = cosA * rdx0 + sinA * rdz0;
                    double rdy = rdy0;
                    double rdz = -sinA * rdx0 + cosA * rdz0;
                    // 归一化方向
                    double rlen = Math.sqrt(rdx * rdx + rdy * rdy + rdz * rdz);
                    rdx /= rlen; rdy /= rlen; rdz /= rlen;
                    // 光线-球体求交
                    double b = rox * rdx + roy * rdy + roz * rdz;
                    double c = rox * rox + roy * roy + roz * roz - radius * radius;
                    double h = b * b - c;
                    if (h < 0) continue;
                    double sqrtH = Math.sqrt(h);
                    double t0 = -b - sqrtH;
                    double t1 = -b + sqrtH;
                    double tHit = -1;
                    if (t0 > 0) {
                        double p1x = rox + rdx * t0;
                        if (p1x >= 0) tHit = t0;
                    }
                    if (tHit < 0 && t1 > 0) {
                        double p2x = rox + rdx * t1;
                        if (p2x >= 0) tHit = t1;
                    }
                    if (tHit <= 0) continue;
                    // 交点 + 法线
                    double pxs = rox + rdx * tHit;
                    double pys = roy + rdy * tHit;
                    double pzs = roz + rdz * tHit;
                    double nlen = Math.sqrt(pxs * pxs + pys * pys + pzs * pzs);
                    double nx = pxs / nlen, ny = pys / nlen, nz = pzs / nlen;
                    double facing = rdx * nx + rdy * ny + rdz * nz;
                    // UV（等距柱状投影）
                    double u = 0.5 + Math.atan2(nz, nx) / (2 * Math.PI);
                    double v = 0.5 + Math.asin(Math.max(-1, Math.min(1, ny))) / Math.PI;
                    int sx = (int) (u * imgW);
                    int sy = (int) (v * imgH);
                    if (sx < 0 || sx >= imgW || sy < 0 || sy >= imgH) continue;
                    int argb = bi.getRGB(sx, sy);
                    if (facing > 0) {
                        // 内表面变暗
                        int a = (argb >>> 24);
                        int r = (int) (((argb >> 16) & 0xFF) * 0.7);
                        int g = (int) (((argb >> 8) & 0xFF) * 0.7);
                        int bb = (int) ((argb & 0xFF) * 0.7);
                        argb = (a << 24) | (r << 16) | (g << 8) | bb;
                    }
                    dst.setRGB(px, py, argb);
                }
            }
            return ImageEdit.fromBufferedImage(dst);
        }, totalFrames, 0.05f);
    }
}
