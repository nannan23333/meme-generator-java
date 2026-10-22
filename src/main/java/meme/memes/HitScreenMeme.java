package meme.memes;

import meme.core.MemeInfo;
import meme.core.MemeParams;
import meme.core.MemeOption;
import meme.utils.GifSupport;
import meme.utils.ImageEdit;
import meme.utils.InputImage;
import meme.utils.MemeResources;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 移植自 memes/hit_screen.rs。 */
@Component
public class HitScreenMeme extends AbstractMeme {

    public HitScreenMeme() {
        super(new MemeInfo("hit_screen",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("打穿", "打穿屏幕"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 21, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 6, 21, 0, 0), ZoneOffset.ofHours(8))));
    }

    // 每项：{p0, p1, p2, p3, pos}，其中 p* 为透视四点、pos 为绘制位置
    private static final int[][][] PARAMS = {
            {{1, 10}, {138, 1}, {140, 119}, {7, 154}, {32, 37}},
            {{1, 10}, {138, 1}, {140, 121}, {7, 154}, {32, 37}},
            {{1, 10}, {138, 1}, {139, 125}, {10, 159}, {32, 37}},
            {{1, 12}, {136, 1}, {137, 125}, {8, 159}, {34, 37}},
            {{1, 9}, {137, 1}, {139, 122}, {9, 154}, {35, 41}},
            {{1, 8}, {144, 1}, {144, 123}, {12, 155}, {30, 45}},
            {{1, 8}, {140, 1}, {141, 121}, {10, 155}, {29, 49}},
            {{1, 9}, {140, 1}, {139, 118}, {10, 153}, {27, 53}},
            {{1, 7}, {144, 1}, {145, 117}, {13, 153}, {19, 57}},
            {{1, 7}, {144, 1}, {143, 116}, {13, 153}, {19, 57}},
            {{1, 8}, {139, 1}, {141, 119}, {12, 154}, {19, 55}},
            {{1, 13}, {140, 1}, {143, 117}, {12, 156}, {16, 57}},
            {{1, 10}, {138, 1}, {142, 117}, {11, 149}, {14, 61}},
            {{1, 10}, {141, 1}, {148, 125}, {13, 153}, {11, 57}},
            {{1, 12}, {141, 1}, {147, 130}, {16, 150}, {11, 60}},
            {{1, 15}, {165, 1}, {175, 135}, {1, 171}, {-6, 46}},
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        return makeGifLoop(images.get(0), (outIdx, inIdx) -> {
            var frame = MemeResources.loadImage("hit_screen/%02d.png".formatted(outIdx));
            if (outIdx < 6 || outIdx >= 22) {
                return frame;
            }
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var image = ImageEdit.resizeFit(images.get(0).image(), 140, 120, ImageEdit.Fit.COVER);
            int[][] p = PARAMS[outIdx - 6];
            var persp = ImageEdit.perspective(image, p[0][0], p[0][1], p[1][0], p[1][1], p[2][0], p[2][1], p[3][0], p[3][1]);
            canvas.drawImage(persp, p[4][0], p[4][1]);
            canvas.drawImage(frame, 0, 0);
            return surface.makeImageSnapshot();
        }, 26, 0.06f);
    }
}
