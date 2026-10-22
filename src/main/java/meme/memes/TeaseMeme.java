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

/** 移植自 memes/tease.rs。 */
@Component
public class TeaseMeme extends AbstractMeme {

    public TeaseMeme() {
        super(new MemeInfo("tease",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("拿捏", "戏弄"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 12, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][][] PARAMS = {
            {{21, 75}, {0, 0}, {129, 3}, {155, 123}, {12, 142}},
            {{18, 73}, {0, 29}, {128, 0}, {149, 118}, {30, 147}},
            {{22, 78}, {0, 37}, {136, 1}, {160, 97}, {16, 152}},
            {{22, 58}, {0, 58}, {169, 1}, {194, 92}, {24, 170}},
            {{43, 23}, {0, 114}, {166, 1}, {168, 98}, {41, 205}},
            {{38, 24}, {0, 112}, {171, 0}, {169, 113}, {45, 195}},
            {{31, 54}, {0, 73}, {148, 0}, {172, 81}, {45, 170}},
            {{24, 62}, {0, 62}, {159, 1}, {177, 81}, {47, 155}},
            {{31, 75}, {1, 45}, {126, 1}, {158, 81}, {29, 145}},
            {{18, 61}, {0, 63}, {161, 1}, {190, 88}, {42, 153}},
            {{20, 66}, {0, 57}, {152, 0}, {195, 82}, {40, 149}},
            {{16, 77}, {0, 41}, {141, 0}, {170, 90}, {27, 138}},
            {{28, 105}, {0, 1}, {132, 0}, {131, 112}, {1, 114}},
            {{21, 107}, {0, 1}, {132, 0}, {131, 112}, {1, 114}},
            {{11, 113}, {1, 7}, {138, 0}, {141, 126}, {4, 131}},
            {{10, 114}, {0, 0}, {142, 0}, {142, 131}, {0, 131}},
            {{5, 121}, {0, 0}, {147, 0}, {147, 115}, {0, 115}},
            {{0, 119}, {0, 0}, {158, 0}, {158, 102}, {0, 102}},
            {{0, 116}, {0, 0}, {158, 0}, {158, 107}, {0, 107}},
            {{0, 119}, {0, 0}, {158, 0}, {158, 103}, {0, 101}},
            {{2, 101}, {0, 0}, {153, 0}, {153, 122}, {0, 120}},
            {{-18, 85}, {61, 0}, {194, 15}, {143, 146}, {0, 133}},
            {{0, 66}, {88, 1}, {173, 17}, {123, 182}, {0, 131}},
            {{0, 29}, {118, 3}, {201, 48}, {111, 220}, {1, 168}}
    };

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.square(images.get(0).image());
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            var frame = MemeResources.loadImage("tease/%02d.png".formatted(i));
            int[][] p = PARAMS[i];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var persp = ImageEdit.perspective(img, p[1][0], p[1][1], p[2][0], p[2][1], p[3][0], p[3][1], p[4][0], p[4][1]);
            canvas.drawImage(persp, p[0][0], p[0][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.05f);
    }
}
