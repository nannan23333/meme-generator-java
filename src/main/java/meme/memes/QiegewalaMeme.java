package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（qiegewala）。 */
@Component
public class QiegewalaMeme extends GifSubtitleMeme {
    public QiegewalaMeme() {
        super("qiegewala", List.of("切格瓦拉"),
                new int[][]{{0, 15}, {16, 31}, {31, 38}, {38, 48}, {49, 68}, {68, 86}},
                List.of("没有钱啊 肯定要做的啊", "不做的话没有钱用", "那你不会去打工啊", "有手有脚的", "打工是不可能打工的", "这辈子不可能打工的"),
                20.0f,
                "2022-1-19", "2023-2-14");
    }
}
