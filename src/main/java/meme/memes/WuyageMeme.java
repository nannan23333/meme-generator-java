package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（wuyage）。 */
@Component
public class WuyageMeme extends GifSubtitleMeme {
    public WuyageMeme() {
        super("wuyage", List.of("乌鸦哥"),
                new int[][]{{8, 32}, {35, 58}, {60, 85}},
                List.of("哟 云崽机器人", "今天掉线了没", "来给他弹个版本过低"),
                18.0f,
                "2024-12-05", "2024-12-05");
    }
}
