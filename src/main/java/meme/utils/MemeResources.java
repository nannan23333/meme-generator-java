package meme.utils;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Image;
import meme.core.MemeError;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 资源访问：字体目录与图片素材。素材直接复用原 Rust 仓库 resources/ 目录。
 */
public final class MemeResources {

    private static volatile Path resourceDir;

    private MemeResources() {
    }

    public static void init(Path dir) {
        resourceDir = dir;
    }

    public static Path resourceDir() {
        Path dir = resourceDir;
        if (dir == null) {
            throw new IllegalStateException("MemeResources not initialized, call init() first");
        }
        return dir;
    }

    public static Path fontsDir() {
        return resourceDir().resolve("fonts");
    }

    /** 加载素材图片（相对 resources/images 的路径），对齐 Rust load_image。 */
    public static Image loadImage(String relPath) {
        Path path = resourceDir().resolve("images").resolve(relPath);
        if (!Files.exists(path)) {
            throw MemeError.assetMissing(relPath);
        }
        try {
            return Image.makeFromEncoded(Files.readAllBytes(path));
        } catch (IOException e) {
            throw MemeError.assetMissing(relPath);
        }
    }

    /** 读取素材原始字节。 */
    public static byte[] readResource(String relPath) {
        Path path = resourceDir().resolve(relPath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw MemeError.assetMissing(relPath);
        }
    }

    public static InputStream openResource(String relPath) {
        try {
            return Files.newInputStream(resourceDir().resolve(relPath));
        } catch (IOException e) {
            throw MemeError.assetMissing(relPath);
        }
    }

    /** 用于 skija Typeface 从文件加载。 */
    public static Data readResourceData(String relPath) {
        return Data.makeFromBytes(readResource(relPath));
    }
}
