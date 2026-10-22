package meme.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** 对齐 Rust MemeShortcut。 */
public record MemeShortcut(
        String pattern,
        String humanized,
        List<String> names,
        List<String> texts,
        Map<String, Object> options) {

    public static MemeShortcut of(String pattern, String humanized, List<String> names, List<String> texts) {
        return new MemeShortcut(pattern, humanized, names, texts, Map.of());
    }
}
