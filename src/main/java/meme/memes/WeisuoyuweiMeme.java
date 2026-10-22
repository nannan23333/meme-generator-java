package meme.memes;

import org.springframework.stereotype.Component;

import java.util.List;

/** 移植自 memes/gif_subtitle.rs（weisuoyuwei）。 */
@Component
public class WeisuoyuweiMeme extends GifSubtitleMeme {
    public WeisuoyuweiMeme() {
        super("weisuoyuwei", List.of("为所欲为"),
                new int[][]{{11, 14}, {27, 38}, {42, 61}, {63, 81}, {82, 95}, {96, 105}, {111, 131}, {145, 157}, {157, 167}},
                List.of("好啊", "别说我是一等良民", "就算你们真的想要诬告我", "我有的是钱请律师帮我打官司", "我想我根本不用坐牢", "有钱了不起啊", "Sorry，有钱真的了不起", "不过我想你不会明白这种感觉", "不明白，不明白"),
                19.0f,
                "2022-1-19", "2023-2-14");
    }
}
