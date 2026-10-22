package meme.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/** 服务配置，对齐 Rust config.toml 的 server / resource 部分。 */
@ConfigurationProperties(prefix = "meme")
public record MemeServerProperties(
        Path resourceDir,
        int maxConcurrentTasks,
        long bodyLimit,
        String disabledList) {

    public MemeServerProperties {
        if (resourceDir == null) {
            resourceDir = Path.of(System.getenv().getOrDefault("MEME_RESOURCE_DIR",
                    "../rs/meme-generator-rs/resources"));
        }
        if (maxConcurrentTasks <= 0) {
            maxConcurrentTasks = 16;
        }
        if (bodyLimit <= 0) {
            bodyLimit = 20L * 1024 * 1024;
        }
        if (disabledList == null) {
            disabledList = "";
        }
    }
}
