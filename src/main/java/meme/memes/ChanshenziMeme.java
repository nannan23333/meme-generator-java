package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（chanshenzi）。 */
@Component
public class ChanshenziMeme extends GifSubtitleMeme {
    public ChanshenziMeme() {
        super("chanshenzi", List.of("馋身子"),
                new int[][]{{0, 16}, {16, 31}, {33, 40}},
                List.of("你那叫喜欢吗？", "你那是馋她身子", "你下贱！"),
                18.0f,
                "2022-1-19", "2023-2-14");
    }
}
