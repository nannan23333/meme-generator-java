package meme.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 业务错误，对齐 Rust meme_generator_core::error::Error 的错误码语义：
 * 510 解码 / 520 编码 / 530 素材缺失 / 540 反序列化 /
 * 550 图片数量不匹配 / 551 文字数量不匹配 / 560 文字超长 / 570 反馈
 */
public class MemeError extends RuntimeException {

    public enum Kind {
        IMAGE_DECODE_ERROR(510, "Image decode error"),
        IMAGE_ENCODE_ERROR(520, "Image encode error"),
        IMAGE_ASSET_MISSING(530, "Image asset missing"),
        DESERIALIZE_ERROR(540, "Deserialize error"),
        IMAGE_NUMBER_MISMATCH(550, "The number of images is mismatch"),
        TEXT_NUMBER_MISMATCH(551, "The number of texts is mismatch"),
        TEXT_OVER_LENGTH(560, "Text over length"),
        MEME_FEEDBACK(570, "Meme feedback");

        final int code;
        final String prefix;

        Kind(int code, String prefix) {
            this.code = code;
            this.prefix = prefix;
        }
    }

    private final Kind kind;
    private final Map<String, Object> data = new LinkedHashMap<>();

    public MemeError(Kind kind, String detail, Map<String, Object> data) {
        super(kind.prefix + (detail == null || detail.isBlank() ? "" : ": " + detail));
        this.kind = kind;
        if (data != null) {
            this.data.putAll(data);
        }
    }

    public static MemeError imageDecode(String detail) {
        return new MemeError(Kind.IMAGE_DECODE_ERROR, detail, Map.of("error", detail));
    }

    public static MemeError imageEncode(String detail) {
        return new MemeError(Kind.IMAGE_ENCODE_ERROR, detail, Map.of("error", detail));
    }

    public static MemeError assetMissing(String path) {
        return new MemeError(Kind.IMAGE_ASSET_MISSING, path, Map.of("path", path));
    }

    public static MemeError deserialize(String detail) {
        return new MemeError(Kind.DESERIALIZE_ERROR, detail, Map.of("error", detail));
    }

    public static MemeError imageNumberMismatch(int min, int max, int actual) {
        return new MemeError(Kind.IMAGE_NUMBER_MISMATCH,
                "expected %d to %d images, but got %d".formatted(min, max, actual),
                Map.of("min", min, "max", max, "actual", actual));
    }

    public static MemeError textNumberMismatch(int min, int max, int actual) {
        return new MemeError(Kind.TEXT_NUMBER_MISMATCH,
                "expected %d to %d texts, but got %d".formatted(min, max, actual),
                Map.of("min", min, "max", max, "actual", actual));
    }

    public static MemeError feedback(String detail) {
        return new MemeError(Kind.MEME_FEEDBACK, detail, Map.of("error", detail));
    }

    public static MemeError textOverLength(String text) {
        return new MemeError(Kind.TEXT_OVER_LENGTH, text, Map.of("text", text));
    }

    public Kind getKind() {
        return kind;
    }

    public int getCode() {
        return kind.code;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
