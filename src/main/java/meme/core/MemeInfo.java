package meme.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/** 对齐 Rust MemeInfo，字段名与顺序保持一致（snake_case）。 */
@JsonPropertyOrder({"key", "params", "keywords", "shortcuts", "tags", "date_created", "date_modified"})
public record MemeInfo(
        String key,
        MemeParams params,
        List<String> keywords,
        List<MemeShortcut> shortcuts,
        Set<String> tags,
        @JsonProperty("date_created") OffsetDateTime dateCreated,
        @JsonProperty("date_modified") OffsetDateTime dateModified) {
}
