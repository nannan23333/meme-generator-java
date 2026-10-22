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

/** 移植自 memes/lim_x_0.rs。 */
@Component
public class LimX0Meme extends AbstractMeme {

    public LimX0Meme() {
        super(new MemeInfo("lim_x_0",
                new MemeParams(1, 1, 0, 0, List.of(), List.of()),
                List.of("等价无穷小"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 3, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    private static final int[][] LOCS = {
            {143, 32}, {155, 148}, {334, 149}, {275, 266}, {486, 266}, {258, 383}, {439, 382},
            {343, 539}, {577, 487}, {296, 717}, {535, 717}, {64, 896}, {340, 896}, {578, 897},
            {210, 1038}, {644, 1039}, {64, 1192}, {460, 1192}, {698, 1192}, {1036, 141},
            {1217, 141}, {1243, 263}, {1140, 378}, {1321, 378}, {929, 531}, {1325, 531},
            {1592, 531}, {1007, 687}, {1390, 687}, {1631, 686}, {1036, 840}, {1209, 839},
            {1447, 839}, {1141, 1018}, {1309, 1019}, {1546, 1019}, {1037, 1197}, {1317, 1198}, {1555, 1197}};

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        var frame = MemeResources.loadImage("lim_x_0/0.png");
        var surface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        var canvas = surface.getCanvas();
        canvas.drawImage(frame, 0, 0);
        var image = ImageEdit.circle(images.get(0).image());
        var imgC = ImageEdit.resizeExact(image, 72, 72);
        var imgTp = ImageEdit.resizeExact(image, 51, 51);
        for (int[] loc : LOCS) {
            canvas.drawImage(imgC, loc[0], loc[1]);
        }
        canvas.drawImage(imgTp, 948, 247);
        return ImageEdit.encodePng(surface.makeImageSnapshot());
    }
}
