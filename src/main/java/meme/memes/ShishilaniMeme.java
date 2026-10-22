package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（shishilani）。 */
@Component
public class ShishilaniMeme extends GifSubtitleMeme {
    public ShishilaniMeme() {
        super("shishilani", List.of("食屎啦你"),
                new int[][]{{14, 21}, {23, 36}, {38, 46}, {60, 66}},
                List.of("穿西装打领带", "拿大哥大有什么用", "跟着这样的大哥", "食屎啦你"),
                17.0f,
                "2022-1-19", "2023-2-14");
    }
}
