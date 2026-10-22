package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（maikease）。 */
@Component
public class MaikeaseMeme extends GifSubtitleMeme {
    public MaikeaseMeme() {
        super("maikease", List.of("麦克阿瑟说"),
                new int[][]{{0, 22}, {24, 46}, {48, 70}, {72, 84}},
                List.of("美国前五星上将麦克阿瑟", "曾这样评价道", "如果让我去阻止xxx", "那么我宁愿去阻止上帝"),
                20.0f,
                "2023-7-30", "2023-7-30");
    }
}
