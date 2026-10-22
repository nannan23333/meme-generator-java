package meme.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** 命令行解析标志，对齐 Rust ParserFlags（snake_case）。 */
public record ParserFlags(
        @JsonProperty("short") boolean shortFlag,
        @JsonProperty("long") boolean longFlag,
        @JsonProperty("short_aliases") List<Character> shortAliases,
        @JsonProperty("long_aliases") List<String> longAliases) {

    public static ParserFlags none() {
        return new ParserFlags(false, false, List.of(), List.of());
    }
}
