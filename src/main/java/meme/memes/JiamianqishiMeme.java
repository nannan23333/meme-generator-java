package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（jiamianqishi）。 */
@Component
public class JiamianqishiMeme extends GifSubtitleMeme {
    public JiamianqishiMeme() {
        super("jiamianqishi", List.of("假面骑士"),
                new int[][]{{2, 9}, {14, 20}, {27, 40}, {45, 62}},
                List.of("哦～", "（飞扑）", "一直想看你这幅表情", "这幅嫉妒我的表情"),
                17.0f,
                "2024-10-30", "2024-10-30");
    }
}
