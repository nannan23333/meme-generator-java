package meme.utils;

import io.github.humbleui.skija.Bitmap;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.EncodedImageFormat;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Path;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.types.Point;
import io.github.humbleui.types.Rect;
import meme.core.MemeError;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * skija 图片编辑工具，对齐 Rust ImageExt（square / circle / resize_fit / rotate / resize_exact 等）。
 */
public final class ImageEdit {

    private ImageEdit() {
    }

    // ---------- 编解码 ----------

    public static Image decode(byte[] data) {
        try {
            Image img = Image.makeFromEncoded(data);
            if (img == null) {
                throw MemeError.imageDecode("skija decode error");
            }
            return img;
        } catch (Exception e) {
            throw MemeError.imageDecode(e.getMessage() == null ? "skija decode error" : e.getMessage());
        }
    }

    public static byte[] encodePng(Image image) {
        try {
            return image.encodeToData(EncodedImageFormat.PNG).getBytes();
        } catch (Exception e) {
            throw MemeError.imageEncode(e.getMessage());
        }
    }

    /** skija Image → BufferedImage（经 PNG 往返，简单可靠）。 */
    public static BufferedImage toBufferedImage(Image image) {
        try {
            byte[] png = encodePng(image);
            return ImageIO.read(new ByteArrayInputStream(png));
        } catch (IOException e) {
            throw MemeError.imageEncode(e.getMessage());
        }
    }

    /** BufferedImage → skija Image。 */
    public static Image fromBufferedImage(BufferedImage image) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bos);
            return Image.makeFromEncoded(bos.toByteArray());
        } catch (IOException e) {
            throw MemeError.imageEncode(e.getMessage());
        }
    }

    // ---------- 几何变换 ----------

    public static Image flipHorizontal(Image image) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Canvas canvas = surface.getCanvas();
            canvas.translate(w, 0);
            canvas.scale(-1, 1);
            canvas.drawImage(image, 0, 0);
            return surface.makeImageSnapshot();
        }
    }

    public static Image flipVertical(Image image) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Canvas canvas = surface.getCanvas();
            canvas.translate(0, h);
            canvas.scale(1, -1);
            canvas.drawImage(image, 0, 0);
            return surface.makeImageSnapshot();
        }
    }

    /** 圆角矩形裁剪（半径像素），对齐 Rust round_corner()。 */
    public static Image roundCorner(Image image, float radius) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h));
             Path path = io.github.humbleui.skija.Path.makeRRect(
                     io.github.humbleui.types.RRect.makeXYWH(0, 0, w, h, radius))) {
            Canvas canvas = surface.getCanvas();
            canvas.save();
            canvas.clipPath(path);
            canvas.drawImage(image, 0, 0);
            canvas.restore();
            return surface.makeImageSnapshot();
        }
    }

    /**
     * 平滑绘制（线性采样）。Skia 的 {@code drawImage} 默认是最近邻采样，
     * 旋转/缩放时会出硬台阶；Rust 侧一律走 default_sampling_options()（linear+mipmap）。
     */
    static void drawSmooth(Canvas canvas, Image image, float x, float y) {
        canvas.drawImageRect(image,
                io.github.humbleui.types.Rect.makeWH(image.getWidth(), image.getHeight()),
                io.github.humbleui.types.Rect.makeXYWH(x, y, image.getWidth(), image.getHeight()),
                io.github.humbleui.skija.SamplingMode.LINEAR, null, false);
    }

    /** 旋转并保持原尺寸（中心旋转裁剪），对齐 Rust rotate_crop()。 */
    public static Image rotateCrop(Image image, float degrees) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Canvas canvas = surface.getCanvas();
            canvas.translate(w / 2f, h / 2f);
            canvas.rotate(degrees);
            canvas.translate(-w / 2f, -h / 2f);
            drawSmooth(canvas, image, 0, 0);
            return surface.makeImageSnapshot();
        }
    }

    /** 旋转（角度制，顺时针），画布扩展到旋转后的外接矩形。 */
    public static Image rotate(Image image, float degrees) {
        float rad = (float) Math.toRadians(degrees);
        int w = image.getWidth();
        int h = image.getHeight();
        double cos = Math.abs(Math.cos(rad));
        double sin = Math.abs(Math.sin(rad));
        int newW = (int) Math.ceil(w * cos + h * sin);
        int newH = (int) Math.ceil(w * sin + h * cos);
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(newW, newH))) {
            Canvas canvas = surface.getCanvas();
            canvas.translate(newW / 2f, newH / 2f);
            canvas.rotate(degrees);
            drawSmooth(canvas, image, -w / 2f, -h / 2f);
            return surface.makeImageSnapshot();
        }
    }

    /**
     * 精确缩放到目标尺寸。
     *
     * <p>缩小时走「逐级折半」再收尾一步，等价于 Rust 侧
     * {@code SamplingOptions::new(FilterMode::Linear, MipmapMode::Linear)}（mipmap 线性）。
     * Skia/skija 的 {@code SamplingMode.LINEAR} 只取 4 个纹素，15 倍这种大倍率缩小会严重漏采样，
     * 边缘直接变成硬台阶（用户看到的"锯齿"）。</p>
     */
    public static Image resizeExact(Image image, int width, int height) {
        Image pre = mipHalve(image, width, height);
        return resizeOnce(pre, 0, 0, pre.getWidth(), pre.getHeight(), width, height, true);
    }

    /**
     * 最近邻缩放，对齐 Rust <code>SamplingOptions::default()</code>（FilterMode::Nearest）。
     * 只用于 Rust 侧显式写了 default() 的地方（目前是 pixelate 的马赛克两段缩放）。
     */
    public static Image resizeExactNearest(Image image, int width, int height) {
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(width, height))) {
            surface.getCanvas().drawImageRect(image,
                    io.github.humbleui.types.Rect.makeWH(image.getWidth(), image.getHeight()),
                    io.github.humbleui.types.Rect.makeWH(width, height),
                    io.github.humbleui.skija.SamplingMode.DEFAULT, null, true);
            return surface.makeImageSnapshot();
        }
    }

    /** 单次 drawImageRect（线性采样），供内部复用。 */
    private static Image resizeOnce(Image image, float sx, float sy, float sw, float sh,
                                    int width, int height, boolean strict) {
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(width, height))) {
            surface.getCanvas().drawImageRect(image,
                    io.github.humbleui.types.Rect.makeXYWH(sx, sy, sw, sh),
                    io.github.humbleui.types.Rect.makeWH(width, height),
                    io.github.humbleui.skija.SamplingMode.LINEAR, null, strict);
            return surface.makeImageSnapshot();
        }
    }

    /**
     * 连续折半直到再也折不到目标尺寸，返回中间图（未折半时原样返回）。
     * 折半用 2x2 平均，效果接近 mipmap 金字塔，避免大倍率缩小的漏采样。
     */
    private static Image mipHalve(Image image, int targetW, int targetH) {
        Image cur = image;
        while (cur.getWidth() >= targetW * 2 && cur.getHeight() >= targetH * 2) {
            int w = Math.max(targetW, cur.getWidth() / 2);
            int h = Math.max(targetH, cur.getHeight() / 2);
            if (w >= cur.getWidth() || h >= cur.getHeight()) {
                break;
            }
            cur = resizeOnce(cur, 0, 0, cur.getWidth(), cur.getHeight(), w, h, true);
        }
        return cur;
    }

    public static Image resizeWidth(Image image, int width) {
        int height = Math.max(1, Math.round(image.getHeight() * (width / (float) image.getWidth())));
        return resizeExact(image, width, height);
    }

    public static Image resizeHeight(Image image, int height) {
        int width = Math.max(1, Math.round(image.getWidth() * (height / (float) image.getHeight())));
        return resizeExact(image, width, height);
    }

    public enum Fit {
        CONTAIN, COVER
    }

    /**
     * 等比缩放以适配/填满目标尺寸，对齐 Rust <code>resize_fit</code>：
     * <b>单次</b> drawImageRect，源矩形用浮点居中（不做"先缩放再整数裁剪"的两段式，
     * 那样会多一次重采样并产生 1~2px 偏移，小画布上非常明显）。
     */
    public static Image resizeFit(Image image, int width, int height, Fit fit) {
        float sw = image.getWidth();
        float sh = image.getHeight();
        float dw = width;
        float dh = height;
        float sx, sy, sW, sH, dx, dy, dW, dH;
        if (fit == Fit.COVER) {
            if (dw / dh > sw / sh) {
                sW = sw;
                sH = sw * dh / dw;
            } else {
                sW = sh * dw / dh;
                sH = sh;
            }
            sx = (sw - sW) / 2f;
            sy = (sh - sH) / 2f;
            dx = 0;
            dy = 0;
            dW = dw;
            dH = dh;
        } else {
            sx = 0;
            sy = 0;
            sW = sw;
            sH = sh;
            if (dw / dh > sw / sh) {
                dW = sw * dh / sh;
                dH = dh;
            } else {
                dW = dw;
                dH = sh * dw / sw;
            }
            dx = (dw - dW) / 2f;
            dy = (dh - dH) / 2f;
        }
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(width, height))) {
            // 大倍率缩小时先折半降采样，否则线性采样只取 4 个纹素 → 边缘锯齿
            Image src = image;
            float fx = 1f, fy = 1f;
            if (dW < sW * 0.5f && dH < sH * 0.5f) {
                Image pre = mipHalve(image, Math.max(1, Math.round(sW)), Math.max(1, Math.round(sH)));
                if (pre != image) {
                    fx = pre.getWidth() / sw;
                    fy = pre.getHeight() / sh;
                    src = pre;
                }
            }
            surface.getCanvas().drawImageRect(src,
                    io.github.humbleui.types.Rect.makeXYWH(sx * fx, sy * fy, sW * fx, sH * fy),
                    io.github.humbleui.types.Rect.makeXYWH(dx, dy, dW, dH),
                    io.github.humbleui.skija.SamplingMode.LINEAR, null, false);
            return surface.makeImageSnapshot();
        }
    }

    /**
     * 对齐 Rust <code>resize_bound</code>：按比例缩放到 bounds 之内/之外，<b>不补边</b>，
     * 返回缩放后的图（尺寸可能与 bounds 不等）。注意 Rust 用的是整数除法。
     */
    public static Image resizeBound(Image image, int width, int height, Fit fit) {
        int sw = image.getWidth();
        int sh = image.getHeight();
        int w;
        int h;
        // Rust 这里是**整数除法**比较比例（1500/1080 == 100/100 == 1），不能用浮点
        if (fit == Fit.CONTAIN) {
            if (width / height > sw / sh) {
                w = width;
                h = sh * width / sw;
            } else {
                w = sw * height / sh;
                h = height;
            }
        } else {
            if (width / height > sw / sh) {
                w = sw * height / sh;
                h = height;
            } else {
                w = width;
                h = sh * width / sw;
            }
        }
        return resizeExact(image, Math.max(1, w), Math.max(1, h));
    }

    public static Image crop(Image image, int left, int top, int right, int bottom) {
        left = Math.max(0, Math.min(left, image.getWidth()));
        top = Math.max(0, Math.min(top, image.getHeight()));
        right = Math.max(0, Math.min(right, image.getWidth()));
        bottom = Math.max(0, Math.min(bottom, image.getHeight()));
        int w = right - left;
        int h = bottom - top;
        if (w <= 0 || h <= 0) {
            throw MemeError.imageDecode("crop rect is empty");
        }
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Rect src = Rect.makeXYWH(left, top, w, h);
            Rect dst = Rect.makeWH(w, h);
            surface.getCanvas().drawImageRect(image, src, dst, io.github.humbleui.skija.SamplingMode.LINEAR, null, true);
            return surface.makeImageSnapshot();
        }
    }

    /** 中心正方形裁剪，对齐 Rust square()。 */
    public static Image square(Image image) {
        int side = Math.min(image.getWidth(), image.getHeight());
        int left = (image.getWidth() - side) / 2;
        int top = (image.getHeight() - side) / 2;
        return crop(image, left, top, left + side, top + side);
    }

    /** 圆形裁剪（透明背景），对齐 Rust circle()。 */
    public static Image circle(Image image) {
        int side = Math.min(image.getWidth(), image.getHeight());
        Image squared = square(image);
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(side, side))) {
            Canvas canvas = surface.getCanvas();
            Path circle = Path.makeOval(Rect.makeWH(side, side));
            canvas.save();
            canvas.clipPath(circle);
            canvas.drawImage(squared, 0, 0);
            canvas.restore();
            return surface.makeImageSnapshot();
        }
    }


    // ---------- 透视变换与滤镜 ----------

    /**
     * 透视变换：将图片四角 (0,0)(w,0)(w,h)(0,h) 映射到给定四点（顺序：左上/右上/右下/左下）。
     * 对齐 Rust perspective()。skija 无现成 API，手解 8 元线性方程。
     */
    public static Image perspective(Image image, float[][] pts) {
        float x1 = pts[0][0], y1 = pts[0][1];
        float x2 = pts[1][0], y2 = pts[1][1];
        float x3 = pts[2][0], y3 = pts[2][1];
        float x4 = pts[3][0], y4 = pts[3][1];
        float w = image.getWidth(), h = image.getHeight();

        float maxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
        float minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
        float maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
        float minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
        int outW = Math.max(1, Math.round(maxX - minX));
        int outH = Math.max(1, Math.round(maxY - minY));

        // 源点：(0,0)(w,0)(w,h)(0,h)；目标：(x1,y1)(x2,y2)(x3,y3)(x4,y4)
        // 解透视矩阵 [a b c; d e f; g h 1]
        double[] sx = {0, w, w, 0};
        double[] sy = {0, 0, h, h};
        double[] dx = {x1, x2, x3, x4};
        double[] dy = {y1, y2, y3, y4};
        double[] m = solvePerspective(sx, sy, dx, dy);

        float a = (float) m[0], b = (float) m[1], c = (float) m[2];
        float d = (float) m[3], e = (float) m[4], f = (float) m[5];
        float g = (float) m[6], hh = (float) m[7];

        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(outW, outH))) {
            Canvas canvas = surface.getCanvas();
            // 对齐 Rust：只 concat 矩阵、不做额外平移；绘制时用线性采样，否则放大后是锯齿边
            canvas.concat(new io.github.humbleui.skija.Matrix33(new float[]{
                    a, b, c, d, e, f, g, hh, 1f}));
            canvas.drawImageRect(image,
                    io.github.humbleui.types.Rect.makeWH(w, h),
                    io.github.humbleui.types.Rect.makeWH(w, h),
                    io.github.humbleui.skija.SamplingMode.LINEAR, null, true);
            return surface.makeImageSnapshot();
        }
    }

    /** 解 8 元方程：sx' = (a*x + b*y + c)/(g*x + h*y + 1)。 */
    private static double[] solvePerspective(double[] sx, double[] sy,
                                             double[] dx, double[] dy) {
        // 8 个未知数 a..h，8 个方程（4 点各 2 个）
        double[][] A = new double[8][9];
        for (int i = 0; i < 4; i++) {
            // a*x + b*y + c - g*x*dx + -h*y*dx = dx
            A[2 * i][0] = sx[i]; A[2 * i][1] = sy[i]; A[2 * i][2] = 1;
            A[2 * i][6] = -sx[i] * dx[i]; A[2 * i][7] = -sy[i] * dx[i];
            A[2 * i][8] = dx[i];
            // d*x + e*y + f - g*x*dy - h*y*dy = dy
            A[2 * i + 1][3] = sx[i]; A[2 * i + 1][4] = sy[i]; A[2 * i + 1][5] = 1;
            A[2 * i + 1][6] = -sx[i] * dy[i]; A[2 * i + 1][7] = -sy[i] * dy[i];
            A[2 * i + 1][8] = dy[i];
        }
        // 高斯消元
        for (int col = 0; col < 8; col++) {
            int pivot = col;
            for (int r = col + 1; r < 8; r++) {
                if (Math.abs(A[r][col]) > Math.abs(A[pivot][col])) pivot = r;
            }
            double[] tmp = A[col]; A[col] = A[pivot]; A[pivot] = tmp;
            double pv = A[col][col];
            if (Math.abs(pv) < 1e-10) continue;
            for (int c2 = col; c2 <= 8; c2++) A[col][c2] /= pv;
            for (int r = 0; r < 8; r++) {
                if (r == col) continue;
                double factor = A[r][col];
                if (factor != 0) {
                    for (int c2 = col; c2 <= 8; c2++) A[r][c2] -= factor * A[col][c2];
                }
            }
        }
        return new double[]{A[0][8], A[1][8], A[2][8], A[3][8], A[4][8], A[5][8], A[6][8], A[7][8]};
    }

    /** 高斯模糊，对齐 Rust gaussian_blur()。 */
    public static Image gaussianBlur(Image image, float sigmaX, float sigmaY) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h));
             Paint paint = new Paint()) {
            var filter = io.github.humbleui.skija.ImageFilter.makeBlur(sigmaX, sigmaY, io.github.humbleui.skija.FilterTileMode.DECAL);
            paint.setImageFilter(filter);
            surface.getCanvas().drawImage(image, 0, 0, paint);
            return surface.makeImageSnapshot();
        }
    }

    /** 像素化（马赛克），对齐常见 pixelate 效果。 */
    public static Image pixelate(Image image, int blockSize) {
        int w = image.getWidth();
        int h = image.getHeight();
        int sw = Math.max(1, w / blockSize);
        int sh = Math.max(1, h / blockSize);
        // Rust 侧用 SamplingOptions::default()（最近邻），不是线性/mipmap
        var small = resizeExactNearest(image, sw, sh);
        return resizeExactNearest(small, w, h);
    }

    /**
     * 逐像素几何重映射（替代 Rust SkSL RuntimeEffect 方案）：
     * mapper(x, y) 返回采样源坐标 {sx, sy}；越界输出透明。
     */
    public static Image remap(Image image,
                              java.util.function.BiFunction<Float, Float, float[]> mapper) {
        BufferedImage src = toBufferedImage(image);
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float[] s = mapper.apply((float) x, (float) y);
                int sx = Math.round(s[0]);
                int sy = Math.round(s[1]);
                if (sx >= 0 && sx < w && sy >= 0 && sy < h) {
                    dst.setRGB(x, y, src.getRGB(sx, sy));
                }
            }
        }
        return fromBufferedImage(dst);
    }


    /** 填白底（透明区域填色），对齐 Rust with_background()。 */
    public static Image withBackground(Image image) {
        return withBackground(image, 0xFFFFFFFF);
    }

    public static Image withBackground(Image image, int argb) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Canvas canvas = surface.getCanvas();
            try (Paint bg = new Paint().setColor(argb)) {
                canvas.drawRect(io.github.humbleui.types.Rect.makeWH(w, h), bg);
            }
            canvas.drawImage(image, 0, 0);
            return surface.makeImageSnapshot();
        }
    }

    /** 蒙版裁剪（mask 的 alpha 之外透明），对齐 Rust clip_mask()。 */
    public static Image applyMask(Image image, Image mask) {
        int w = image.getWidth();
        int h = image.getHeight();
        var resizedMask = resizeExact(mask, w, h);
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h));
             Paint paint = new Paint()) {
            Canvas canvas = surface.getCanvas();
            // DST_IN：用 mask 的 alpha 裁剪已有内容
            canvas.drawImage(image, 0, 0);
            paint.setBlendMode(io.github.humbleui.skija.BlendMode.DST_IN);
            canvas.drawImage(resizedMask, 0, 0, paint);
            return surface.makeImageSnapshot();
        }
    }

    /**
     * 染色（保明度，只替换色相/饱和度），对齐 Rust <code>colorize()</code>：
     * <code>color_filter(color_filters::blend(color, BlendMode::Color))</code>。
     */
    public static Image colorize(Image image, int argb) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h));
             Paint paint = new Paint();
             var filter = io.github.humbleui.skija.ColorFilter.makeBlend(
                     argb, io.github.humbleui.skija.BlendMode.COLOR)) {
            paint.setColorFilter(filter);
            surface.getCanvas().drawImage(image, 0, 0, paint);
            return surface.makeImageSnapshot();
        }
    }

    /** 透明度调整（factor 0~1），对齐 Rust transparency()。 */
    public static Image transparency(Image image, float factor) {
        float[] m = {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, factor, 0
        };
        return applyColorMatrix(image, m);
    }

    /** 亮度调整，对齐 Rust brightness()。 */
    public static Image brightness(Image image, float factor) {
        float[] m = {
                factor, 0, 0, 0, 0,
                0, factor, 0, 0, 0,
                0, 0, factor, 0, 0,
                0, 0, 0, 1, 0
        };
        return applyColorMatrix(image, m);
    }

    /** 便捷：float 二维点转 perspective 参数。 */
    public static Image perspective(Image image, float x1, float y1, float x2, float y2,
                                    float x3, float y3, float x4, float y4) {
        return perspective(image, new float[][]{{x1, y1}, {x2, y2}, {x3, y3}, {x4, y4}});
    }

    // ---------- 颜色 ----------

    public static Image grayscale(Image image) {
        // ITU-R BT.601 亮度矩阵
        float[] m = {
                0.2126f, 0.7152f, 0.0722f, 0, 0,
                0.2126f, 0.7152f, 0.0722f, 0, 0,
                0.2126f, 0.7152f, 0.0722f, 0, 0,
                0, 0, 0, 1, 0
        };
        return applyColorMatrix(image, m);
    }

    public static Image invert(Image image) {
        float[] m = {
                -1, 0, 0, 0, 1,
                0, -1, 0, 0, 1,
                0, 0, -1, 0, 1,
                0, 0, 0, 1, 0
        };
        return applyColorMatrix(image, m);
    }

    static Image applyColorMatrix(Image image, float[] matrix) {
        int w = image.getWidth();
        int h = image.getHeight();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h));
             Paint paint = new Paint().setColorFilter(io.github.humbleui.skija.ColorFilter.makeMatrix(new io.github.humbleui.skija.ColorMatrix(matrix)))) {
            surface.getCanvas().drawImage(image, 0, 0, paint);
            return surface.makeImageSnapshot();
        }
    }

    // ---------- 合并 ----------

    public static Image mergeHorizontal(java.util.List<Image> images) {
        int w = images.stream().mapToInt(Image::getWidth).sum();
        int h = images.stream().mapToInt(Image::getHeight).max().orElse(1);
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Canvas canvas = surface.getCanvas();
            float x = 0;
            for (Image img : images) {
                canvas.drawImage(img, x, 0);
                x += img.getWidth();
            }
            return surface.makeImageSnapshot();
        }
    }

    public static Image mergeVertical(java.util.List<Image> images) {
        int w = images.stream().mapToInt(Image::getWidth).max().orElse(1);
        int h = images.stream().mapToInt(Image::getHeight).sum();
        try (Surface surface = Surface.makeRaster(ImageInfo.makeN32Premul(w, h))) {
            Canvas canvas = surface.getCanvas();
            float y = 0;
            for (Image img : images) {
                canvas.drawImage(img, 0, y);
                y += img.getHeight();
            }
            return surface.makeImageSnapshot();
        }
    }

    /** 绘制辅助：在画布指定位置画图（等比缩放到目标宽高）。 */
    public static void drawImageResized(Canvas canvas, Image image, float x, float y, int w, int h) {
        Rect src = Rect.makeWH(image.getWidth(), image.getHeight());
        Rect dst = Rect.makeXYWH(x, y, w, h);
        canvas.drawImageRect(image, src, dst, io.github.humbleui.skija.SamplingMode.LINEAR, null, true);
    }
}
