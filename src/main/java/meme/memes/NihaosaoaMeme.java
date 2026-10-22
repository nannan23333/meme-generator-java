package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（nihaosaoa）。 */
@Component
public class NihaosaoaMeme extends GifSubtitleMeme {
    public NihaosaoaMeme() {
        super("nihaosaoa", List.of("你好骚啊"),
                new int[][]{{0, 14}, {16, 26}, {42, 61}},
                List.of("既然追求刺激", "就贯彻到底了", "你好骚啊"),
                17.0f,
                "2022-1-19", "2023-2-14");
    }
}
