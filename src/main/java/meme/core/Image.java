package meme.core;

/** 输入图片（name + 原始字节），对齐 Rust meme::Image。 */
public record Image(String name, byte[] data) {
}
