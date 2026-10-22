package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（zengxiaoxian）。 */
@Component
public class ZengxiaoxianMeme extends GifSubtitleMeme {
    public ZengxiaoxianMeme() {
        super("zengxiaoxian", List.of("曾小贤"),
                new int[][]{{3, 15}, {24, 30}, {30, 46}, {56, 63}},
                List.of("平时你打电子游戏吗", "偶尔", "星际还是魔兽", "连连看"),
                21.0f,
                "2022-1-19", "2023-2-14");
    }
}
