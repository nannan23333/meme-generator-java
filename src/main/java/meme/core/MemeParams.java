package meme.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** 对齐 Rust MemeParams（snake_case）。 */
public record MemeParams(
        @JsonProperty("min_images") int minImages,
        @JsonProperty("max_images") int maxImages,
        @JsonProperty("min_texts") int minTexts,
        @JsonProperty("max_texts") int maxTexts,
        @JsonProperty("default_texts") List<String> defaultTexts,
        List<MemeOption> options) {

    public static MemeParams empty() {
        return new MemeParams(0, 0, 0, 0, List.of(), List.of());
    }
}
