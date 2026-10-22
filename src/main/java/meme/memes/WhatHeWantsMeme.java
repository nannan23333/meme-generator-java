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

/** 移植自 memes/what_he_wants.rs。 */
@Component
public class WhatHeWantsMeme extends AbstractMeme {

    public WhatHeWantsMeme() {
        super(new MemeInfo("what_he_wants",
                new MemeParams(1, 1, 0, 1, List.of("今年520"), List.of()),
                List.of("最想要的东西"), List.of(), Set.of(),
                OffsetDateTime.of(LocalDateTime.of(2022, 3, 14, 0, 0), ZoneOffset.ofHours(8)),
                OffsetDateTime.of(LocalDateTime.of(2023, 2, 14, 0, 0), ZoneOffset.ofHours(8))));
    }

    @Override
    protected byte[] doGenerate(List<InputImage> images, List<String> texts, Map<String, Object> options) {
        String date = texts.isEmpty() ? "今年520" : texts.get(0);
        String text = date + "我会给你每个男人都最想要的东西···";
        var frame = MemeResources.loadImage("what_he_wants/0.png");
        var textSurface = meme.utils.TextRenderer.newSurface(frame.getWidth(), frame.getHeight());
        textSurface.getCanvas().drawImage(frame, 0, 0);
        meme.utils.TextRenderer.drawTextAreaAutoFontSize(
                textSurface.getCanvas(),
                io.github.humbleui.types.Rect.makeLTRB(0, 514, 1024, 614),
                text, 20f, 80f,
                meme.utils.TextRenderer.params().fill(0xFF000000).stroke(0.125f, 0xFFFFFFFF)
                        .fontFamilies("NotoSansSC-Bold.ttf"));
        var composed = textSurface.makeImageSnapshot();
        return InputImage.makePngOrGif(images.get(0), input -> {
            var surface = meme.utils.TextRenderer.newSurface(composed.getWidth(), composed.getHeight());
            var canvas = surface.getCanvas();
            canvas.drawImage(composed, 0, 0);
            var img = ImageEdit.resizeFit(input, 538, 538, ImageEdit.Fit.CONTAIN);
            canvas.drawImage(img, 486, 616);
            return surface.makeImageSnapshot();
        });
    }
}
