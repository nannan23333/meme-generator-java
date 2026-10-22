package meme.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

/**
 * 图片暂存：内容 MD5 寻址，目录 MEME_HOME/tmp，10 分钟定时清理。
 * 对齐 Rust server 的 temp file 机制（保证 API 行为一致）。
 */
@Service
public class TempFileService {

    private static final Logger log = LoggerFactory.getLogger(TempFileService.class);

    private final Path tempDir;

    public TempFileService() {
        String memeHome = System.getenv("MEME_HOME");
        Path home = (memeHome != null && !memeHome.isBlank())
                ? Path.of(memeHome)
                : Path.of(System.getProperty("user.home"), ".meme_generator");
        this.tempDir = home.resolve("tmp");
        try {
            // 启动时清空暂存目录（对齐 Rust clear_temp_dir）
            if (Files.exists(tempDir)) {
                try (var stream = Files.list(tempDir)) {
                    stream.forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignore) {
                        }
                    });
                }
            }
            Files.createDirectories(tempDir);
        } catch (IOException e) {
            log.warn("Failed to prepare temp dir {}: {}", tempDir, e.getMessage());
        }
    }

    /** 存入数据，返回 MD5 id（已存在则直接复用）。 */
    public String create(byte[] data) {
        String id = md5Hex(data);
        Path path = tempDir.resolve(id);
        if (!Files.exists(path)) {
            try {
                Files.write(path, data);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write temp file: " + e.getMessage(), e);
            }
        }
        return id;
    }

    /** 按 id 读取数据；不存在返回 null。 */
    public byte[] get(String id) {
        Path path = tempDir.resolve(id);
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read temp file: " + e.getMessage(), e);
        }
    }

    /** 每 10 分钟清理 10 分钟前的文件。 */
    @Scheduled(fixedDelay = 10 * 60 * 1000L, initialDelay = 10 * 60 * 1000L)
    public void cleanup() {
        if (!Files.exists(tempDir)) {
            return;
        }
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(10));
        int deleted = 0;
        try (var stream = Files.list(tempDir)) {
            for (Path p : stream.toList()) {
                try {
                    if (Files.isRegularFile(p)
                            && Files.getLastModifiedTime(p).toInstant().isBefore(cutoff)) {
                        Files.deleteIfExists(p);
                        deleted++;
                    }
                } catch (IOException ignore) {
                }
            }
        } catch (IOException e) {
            log.warn("Temp cleanup failed: {}", e.getMessage());
        }
        if (deleted > 0) {
            log.info("Cleaned up {} temporary files", deleted);
        }
    }

    private static String md5Hex(byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
