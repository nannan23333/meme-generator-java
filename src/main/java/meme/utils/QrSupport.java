package meme.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import io.github.humbleui.skija.Image;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** 二维码生成（ZXing），对齐 Rust qrcode_image（Version 5 + EcLevel Q + 无静区）。 */
public final class QrSupport {

    private QrSupport() {
    }

    public static Image qrImage(String message) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.Q);
            hints.put(EncodeHintType.MARGIN, 0); // quiet_zone(false)
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            var matrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, 300, 300, hints);
            BufferedImage bi = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bi.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return ImageEdit.fromBufferedImage(bi);
        } catch (Exception e) {
            throw new RuntimeException("qrcode encode failed", e);
        }
    }
}
