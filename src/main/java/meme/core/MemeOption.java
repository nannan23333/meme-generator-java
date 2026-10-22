package meme.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 表情选项定义，对齐 Rust MemeOption：
 * serde(tag = "type", rename_all = "snake_case") →
 * {"type":"boolean","name":...,"default":...,"description":...,"parser_flags":{...}}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MemeOption.BooleanOption.class, name = "boolean"),
        @JsonSubTypes.Type(value = MemeOption.StringOption.class, name = "string"),
        @JsonSubTypes.Type(value = MemeOption.IntegerOption.class, name = "integer"),
        @JsonSubTypes.Type(value = MemeOption.FloatOption.class, name = "float"),
})
public sealed interface MemeOption {

    String name();

    BooleanOption booleanOption(String name, Boolean defaultValue, String description);

    record BooleanOption(
            String name,
            @JsonProperty("default") Boolean defaultValue,
            String description,
            @JsonProperty("parser_flags") ParserFlags parserFlags) implements MemeOption {

        public BooleanOption(String name, Boolean defaultValue) {
            this(name, defaultValue, null, ParserFlags.none());
        }

        @Override
        public BooleanOption booleanOption(String n, Boolean v, String d) {
            return new BooleanOption(n, v, d, ParserFlags.none());
        }
    }

    record StringOption(
            String name,
            @JsonProperty("default") String defaultValue,
            List<String> choices,
            String description,
            @JsonProperty("parser_flags") ParserFlags parserFlags) implements MemeOption {

        public StringOption(String name, String defaultValue) {
            this(name, defaultValue, null, null, ParserFlags.none());
        }

        @Override
        public BooleanOption booleanOption(String n, Boolean v, String d) {
            throw new UnsupportedOperationException();
        }
    }

    record IntegerOption(
            String name,
            @JsonProperty("default") Integer defaultValue,
            Integer minimum,
            Integer maximum,
            String description,
            @JsonProperty("parser_flags") ParserFlags parserFlags) implements MemeOption {

        public IntegerOption(String name, Integer defaultValue) {
            this(name, defaultValue, null, null, null, ParserFlags.none());
        }

        @Override
        public BooleanOption booleanOption(String n, Boolean v, String d) {
            throw new UnsupportedOperationException();
        }
    }

    record FloatOption(
            String name,
            @JsonProperty("default") Float defaultValue,
            Float minimum,
            Float maximum,
            String description,
            @JsonProperty("parser_flags") ParserFlags parserFlags) implements MemeOption {

        public FloatOption(String name, Float defaultValue) {
            this(name, defaultValue, null, null, null, ParserFlags.none());
        }

        @Override
        public BooleanOption booleanOption(String n, Boolean v, String d) {
            throw new UnsupportedOperationException();
        }
    }

    static BooleanOption bool(String name, Boolean defaultValue) {
        return new BooleanOption(name, defaultValue);
    }
}
