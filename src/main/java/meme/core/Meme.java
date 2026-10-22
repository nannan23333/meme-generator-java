package meme.core;

import java.util.List;
import java.util.Map;

/**
 * 表情契约，对齐 Rust meme_generator_core::meme::Meme。
 */
public interface Meme {

    String key();

    MemeInfo info();

    /**
     * 生成表情图，返回 PNG 或 GIF 字节。
     *
     * @param images  输入图片（name + 原始字节）
     * @param texts   输入文字
     * @param options 选项（值为 Boolean / String / Integer / Double，对应 Rust OptionValue untagged）
     */
    byte[] generate(List<Image> images, List<String> texts, Map<String, Object> options);

    /** 生成预览图（占位图片 + 占位/默认文字）。 */
    byte[] generatePreview(Map<String, Object> options);
}
