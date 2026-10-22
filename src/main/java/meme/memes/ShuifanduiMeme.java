package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（shuifandui）。 */
@Component
public class ShuifanduiMeme extends GifSubtitleMeme {
    public ShuifanduiMeme() {
        super("shuifandui", List.of("谁反对"),
                new int[][]{{3, 14}, {21, 26}, {31, 38}, {40, 45}},
                List.of("我话说完了", "谁赞成", "谁反对", "我反对"),
                19.0f,
                "2022-1-19", "2023-2-14");
    }
}
