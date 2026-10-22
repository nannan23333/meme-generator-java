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

/** 移植自 memes/operator_generator.rs。 */
@Component
public class OperatorGeneratorMeme extends AbstractMeme {

    public OperatorGeneratorMeme() {
        super(new MemeInfo("operator_generator",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("合成大干员"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 28, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final String[] PARTS = {"range", "characteristic", "value", "talent", "skill", "special"};
    private static final int[][] POSITIONS = {{0, 100}, {320, 100}, {0, 280}, {320, 280}, {0, 460}, {320, 460}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String name = images.get(0).name();
        var img = ImageEdit.resizeExact(ImageEdit.circle(images.get(0).image()), 80, 80);
        var surface = meme.utils.TextRenderer.newSurface(640, 640);
        var canvas = surface.getCanvas();
        canvas.clear(0xFFA0A0A0);
        canvas.drawImage(img, 20, 10);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                canvas,
                io.github.humbleui.types.Rect.makeLTRB(120, 0, 620, 100),
                name + "，你的干员信息如下：", 30f, 80f,
                meme.utils.TextRenderer.params().fill(0xFFFFFFFF)
                        .strokePx(15f, 0xFF000000).fontFamilies("NotoSansSC-Bold.ttf"));
        for (int i = 0; i < 6; i++) {
            var part = MemeResources.loadImage(
                    "operator_generator/%s/%02d.jpg".formatted(PARTS[i], (int) (Math.random() * 25)));
            canvas.drawImage(ImageEdit.resizeWidth(part, 320), POSITIONS[i][0], POSITIONS[i][1]);
        }
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
