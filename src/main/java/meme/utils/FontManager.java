package meme.utils;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.Typeface;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字体管理：按文件名 / 家族名查找，懒加载并缓存。
 * 对齐 Rust 侧 FontManager（注册 resources/fonts 下所有字体）。
 */
public final class FontManager {

    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        // 常用别名（Rust 代码里出现的字体名 → 实际字体文件）
        ALIASES.put("fzkatong-m19s", "FZKATJW.ttf");
        ALIASES.put("fzkatong", "FZKATJW.ttf");
    }

    private static final Map<String, Typeface> CACHE = new ConcurrentHashMap<>();
    private static final String DEFAULT_FONT_FILE = "NotoSansSC-Regular.ttf";

    private FontManager() {
    }

    /** 创建字体：name 可为字体文件名（带/不带扩展名）、家族名或别名；找不到则回退默认字体。 */
    public static Font font(String name, float size) {
        Typeface typeface = typeface(name);
        return new Font(typeface, size);
    }

    public static Typeface typeface(String name) {
        String key = normalize(name);
        String mapped = ALIASES.getOrDefault(key, key);
        return CACHE.computeIfAbsent(mapped, FontManager::load);
    }

    public static Typeface defaultTypeface() {
        return typeface(DEFAULT_FONT_FILE);
    }

    private static Typeface load(String key) {
        Path fontsDir = MemeResources.fontsDir();
        // 1) 直接按文件名（试常见扩展名）
        for (String ext : new String[]{"", ".ttf", ".ttc", ".otf"}) {
            Path p = fontsDir.resolve(key + ext);
            if (Files.exists(p)) {
                return FontMgr.getDefault().makeFromFile(p.toString());
            }
        }
        // 2) 遍历目录：匹配文件名 base 或字体内嵌 family name
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fontsDir)) {
            for (Path p : stream) {
                String fileName = p.getFileName().toString();
                String base = fileName.replaceAll("(?i)\\.(ttf|ttc|otf)$", "");
                if (normalize(base).equals(key) || normalize(fileName).equals(key)) {
                    return FontMgr.getDefault().makeFromFile(p.toString());
                }
                try (Typeface tf = FontMgr.getDefault().makeFromFile(p.toString())) {
                    String family = tf.getFamilyName();
                    if (family != null && normalize(family).equals(key)) {
                        tf.close();
                        return FontMgr.getDefault().makeFromFile(p.toString());
                    }
                } catch (Exception ignore) {
                    // 个别字体加载失败跳过
                }
            }
        } catch (IOException e) {
            // fallthrough
        }
        // 3) 回退默认
        return FontMgr.getDefault().makeFromFile(fontsDir.resolve(DEFAULT_FONT_FILE).toString());
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
    }
}
