package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（wangjingze）。 */
@Component
public class WangjingzeMeme extends GifSubtitleMeme {
    public WangjingzeMeme() {
        super("wangjingze", List.of("王境泽"),
                new int[][]{{0, 9}, {12, 24}, {25, 35}, {37, 48}},
                List.of("我就是饿死", "死外边 从这里跳下去", "不会吃你们一点东西", "真香"),
                20.0f,
                "2021-12-24", "2023-2-14");
    }
}
