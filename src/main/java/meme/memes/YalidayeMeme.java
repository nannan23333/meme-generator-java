package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（yalidaye）。 */
@Component
public class YalidayeMeme extends GifSubtitleMeme {
    public YalidayeMeme() {
        super("yalidaye", List.of("压力大爷"),
                new int[][]{{0, 16}, {21, 47}, {52, 77}},
                List.of("外界都说我们压力大", "我觉得吧压力也没有那么大", "主要是28岁了还没媳妇儿"),
                21.0f,
                "2022-1-19", "2023-2-14");
    }
}
