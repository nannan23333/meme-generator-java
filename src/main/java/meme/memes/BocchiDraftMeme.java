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

/** 移植自 memes/bocchi_draft.rs。 */
@Component
public class BocchiDraftMeme extends AbstractMeme {

    public BocchiDraftMeme() {
        super(new MemeInfo("bocchi_draft",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("波奇手稿"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 10, 26, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][][] PARAMS = {
            {{54, 62}, {353, 1}, {379, 382}, {1, 399}}, {{54, 61}, {349, 1}, {379, 381}, {1, 398}},
            {{54, 61}, {349, 1}, {379, 381}, {1, 398}}, {{54, 61}, {335, 1}, {379, 381}, {1, 398}},
            {{54, 61}, {335, 1}, {370, 381}, {1, 398}}, {{41, 59}, {321, 1}, {357, 379}, {1, 396}},
            {{41, 57}, {315, 1}, {357, 377}, {1, 394}}, {{41, 56}, {309, 1}, {353, 380}, {1, 393}},
            {{41, 56}, {314, 1}, {353, 380}, {1, 393}}, {{41, 50}, {312, 1}, {348, 367}, {1, 387}},
            {{35, 50}, {306, 1}, {342, 367}, {1, 386}}};
    private static final int[][] POS = {{146, 173}, {146, 174}, {152, 174}, {158, 167}, {157, 149},
            {167, 108}, {173, 69}, {175, 43}, {174, 30}, {171, 18}, {178, 14}};
    private static final int[] IDX = {0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var image = ImageEdit.resizeFit(images.get(0).image(), 350, 400, ImageEdit.Fit.COVER);
        List<io.github.humbleui.skija.Image> frames = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            var frame = MemeResources.loadImage("bocchi_draft/%02d.png".formatted(i));
            int idx = IDX[i];
            int[][] p = PARAMS[idx];
            var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
            var canvas = surface.getCanvas();
            var persp = ImageEdit.perspective(image, p[0][0], p[0][1], p[1][0], p[1][1], p[2][0], p[2][1], p[3][0], p[3][1]);
            canvas.drawImage(persp, POS[idx][0], POS[idx][1]);
            canvas.drawImage(frame, 0, 0);
            frames.add(surface.makeImageSnapshot());
        }
        return GifSupport.writeGifFromSkija(frames, 0.08f);
    }
}
