package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（wunian）。 */
@Component
public class WunianMeme extends GifSubtitleMeme {
    public WunianMeme() {
        super("wunian", List.of("五年怎么过的"),
                new int[][]{{11, 20}, {35, 50}, {59, 77}, {82, 95}},
                List.of("五年", "你知道我这五年是怎么过的吗", "我每天躲在家里玩贪玩蓝月", "你知道有多好玩吗"),
                16.0f,
                "2022-1-19", "2023-2-14");
    }
}
