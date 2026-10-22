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

/** 移植自 memes/tankuku_raisesign.rs。 */
@Component
public class TankukuRaisesignMeme extends AbstractMeme {

    public TankukuRaisesignMeme() {
        super(new MemeInfo("tankuku_raisesign",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("唐可可举牌"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 5, 26, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][][] PARAMS = {
            {{0, 46}, {320, 0}, {350, 214}, {38, 260}}, {{18, 0}, {328, 28}, {298, 227}, {0, 197}},
            {{15, 0}, {294, 28}, {278, 216}, {0, 188}}, {{14, 0}, {279, 27}, {262, 205}, {0, 178}},
            {{14, 0}, {270, 25}, {252, 195}, {0, 170}}, {{15, 0}, {260, 25}, {242, 186}, {0, 164}},
            {{10, 0}, {245, 21}, {230, 180}, {0, 157}}, {{13, 0}, {230, 21}, {218, 168}, {0, 147}},
            {{13, 0}, {220, 23}, {210, 167}, {0, 140}}, {{27, 0}, {226, 46}, {196, 182}, {0, 135}},
            {{27, 0}, {226, 46}, {196, 182}, {0, 135}}, {{27, 0}, {226, 46}, {196, 182}, {0, 135}},
            {{0, 35}, {200, 0}, {224, 133}, {25, 169}}, {{0, 35}, {200, 0}, {224, 133}, {25, 169}},
            {{0, 35}, {200, 0}, {224, 133}, {25, 169}}};
    private static final int[][] POS = {{68, 91}, {184, 77}, {194, 65}, {203, 55}, {209, 49},
            {215, 41}, {223, 35}, {231, 25}, {238, 21}, {254, 13}, {254, 13}, {254, 13},
            {175, 9}, {195, 17}, {195, 17}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var img = ImageEdit.resizeFit(images.get(0).image(), 300, 230, ImageEdit.Fit.COVER);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            var frame = MemeResources.loadImage("tankuku_raisesign/%02d.png".formatted(i));
            int[][] p = PARAMS[i];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var persp = ImageEdit.perspective(img, p[0][0], p[0][1], p[1][0], p[1][1], p[2][0], p[2][1], p[3][0], p[3][1]);
            canvas.drawImage(persp, POS[i][0], POS[i][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.2f);
    }
}
