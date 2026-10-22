package meme.server;

import meme.utils.MemeResources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "meme")
@ConfigurationPropertiesScan
@EnableScheduling
public class MemeServerApplication {

    public static void main(String[] args) {
        preInitSkijaClasses();
        SpringApplication app = new SpringApplication(MemeServerApplication.class);
        app.run(args);
    }

    /**
     * 启动前单线程预初始化 skija 核心类。
     * 规避运行期并发类初始化死锁：skija 的 Image/Surface 等 <clinit> 都要走
     * Library.staticLoad（同一把 Library.class 锁 + native 加载），若两个请求线程
     * 同时首次触碰不同 skija 类，会形成 AB-BA 死锁（实测发生过）。
     * 在 main 里串行触发全部 clinit 后，运行期不再有竞态。
     */
    private static void preInitSkijaClasses() {
        String[] classes = {
                "io.github.humbleui.skija.impl.Library",
                "io.github.humbleui.skija.Image",
                "io.github.humbleui.skija.Surface",
                "io.github.humbleui.skija.Canvas",
                "io.github.humbleui.skija.Paint",
                "io.github.humbleui.skija.Font",
                "io.github.humbleui.skija.Typeface",
                "io.github.humbleui.skija.FontMgr",
                "io.github.humbleui.skija.ImageInfo",
                "io.github.humbleui.skija.Codec",
                "io.github.humbleui.skija.Data",
        };
        ClassLoader cl = MemeServerApplication.class.getClassLoader();
        for (String name : classes) {
            try {
                Class.forName(name, true, cl);
            } catch (Throwable e) {
                System.err.println("[preInit] skip " + name + ": " + e);
            }
        }
    }

    /** 初始化资源目录（供 meme-utils 静态访问）。 */
    @org.springframework.context.annotation.Bean
    static MemeResourcesInitializer memeResourcesInitializer(MemeServerProperties props) {
        return new MemeResourcesInitializer(props);
    }

    record MemeResourcesInitializer(MemeServerProperties props) {
        @jakarta.annotation.PostConstruct
        void init() {
            MemeResources.init(props.resourceDir());
        }
    }
}
