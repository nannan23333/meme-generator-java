package meme.registry;

import java.util.Locale;

/** 排序维度，值与 Rust MemeSortBy 的 serde snake_case 序列化一致。 */
public enum MemeSortBy {
    KEY("key"),
    KEYWORDS("keywords"),
    KEYWORDS_PINYIN("keywords_pinyin"),
    DATE_CREATED("date_created"),
    DATE_MODIFIED("date_modified");

    private final String value;

    MemeSortBy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static MemeSortBy fromValue(String v) {
        if (v == null || v.isBlank()) {
            return KEY;
        }
        for (MemeSortBy s : values()) {
            if (s.value.equals(v.toLowerCase(Locale.ROOT))) {
                return s;
            }
        }
        return KEY;
    }
}
