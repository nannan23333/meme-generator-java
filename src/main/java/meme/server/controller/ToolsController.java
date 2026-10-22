package meme.server.controller;

import meme.core.Meme;
import meme.registry.MemeRegistryService;
import meme.registry.MemeSortBy;
import meme.server.MemeServerProperties;
import meme.server.TempFileService;
import meme.server.dto.ApiDtos.*;
import meme.server.GlobalExceptionHandler;
import meme.utils.ImageOps;
import meme.utils.TextRenderer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/** tools 端点：render_list / render_statistics / image_operations。 */
@RestController
@RequestMapping("/tools")
public class ToolsController {

    private final TempFileService tempFiles;
    private final MemeRegistryService registry;
    private final Semaphore semaphore;

    public ToolsController(TempFileService tempFiles, MemeRegistryService registry,
                           MemeServerProperties props) {
        this.tempFiles = tempFiles;
        this.registry = registry;
        this.semaphore = new Semaphore(props.maxConcurrentTasks());
    }

    // ---------- render_list / render_statistics ----------

    @PostMapping("/render_list")
    public ImageResponse renderList(@RequestBody(required = false) RenderMemeListParams params) {
        RenderMemeListParams p = params == null ? emptyListParams() : fillDefaults(params);
        try {
            semaphore.acquire();
            try {
                byte[] png = renderListImage(p);
                return new ImageResponse(tempFiles.create(png));
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/render_statistics")
    public ImageResponse renderStatistics(@RequestBody(required = false) RenderMemeStatisticsParams params) {
        try {
            semaphore.acquire();
            try {
                byte[] png = renderStatisticsImage(params);
                return new ImageResponse(tempFiles.create(png));
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    // ---------- image_operations ----------

    @PostMapping("/image_operations/inspect")
    public ImageOps.ImageInfo inspect(@RequestBody ImageRequestId payload) {
        return withSemaphore(() -> ImageOps.inspect(getImage(payload.imageId())));
    }

    @PostMapping("/image_operations/flip_horizontal")
    public ImageResponse flipHorizontal(@RequestBody ImageRequestId payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.flipHorizontal(getImage(payload.imageId()))));
    }

    @PostMapping("/image_operations/flip_vertical")
    public ImageResponse flipVertical(@RequestBody ImageRequestId payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.flipVertical(getImage(payload.imageId()))));
    }

    @PostMapping("/image_operations/rotate")
    public ImageResponse rotate(@RequestBody RotateRequest payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.rotate(getImage(payload.imageId()), payload.degrees())));
    }

    @PostMapping("/image_operations/resize")
    public ImageResponse resize(@RequestBody ResizeRequest payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.resize(getImage(payload.imageId()), payload.width(), payload.height())));
    }

    @PostMapping("/image_operations/crop")
    public ImageResponse crop(@RequestBody CropRequest payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.crop(getImage(payload.imageId()), payload.left(), payload.top(),
                        payload.right(), payload.bottom())));
    }

    @PostMapping("/image_operations/grayscale")
    public ImageResponse grayscale(@RequestBody ImageRequestId payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.grayscale(getImage(payload.imageId()))));
    }

    @PostMapping("/image_operations/invert")
    public ImageResponse invert(@RequestBody ImageRequestId payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.invert(getImage(payload.imageId()))));
    }

    @PostMapping("/image_operations/merge_horizontal")
    public ImageResponse mergeHorizontal(@RequestBody ImagesRequest payload) {
        List<byte[]> images = getImages(payload.imageIds());
        return imageResult(withSemaphore(() -> ImageOps.mergeHorizontal(images)));
    }

    @PostMapping("/image_operations/merge_vertical")
    public ImageResponse mergeVertical(@RequestBody ImagesRequest payload) {
        List<byte[]> images = getImages(payload.imageIds());
        return imageResult(withSemaphore(() -> ImageOps.mergeVertical(images)));
    }

    @PostMapping("/image_operations/gif_split")
    public ImagesResponse gifSplit(@RequestBody ImageRequestId payload) {
        List<byte[]> frames = withSemaphore(() ->
                ImageOps.gifSplit(getImage(payload.imageId())));
        List<String> ids = new ArrayList<>();
        for (byte[] f : frames) {
            ids.add(tempFiles.create(f));
        }
        return new ImagesResponse(ids);
    }

    @PostMapping("/image_operations/gif_merge")
    public ImageResponse gifMerge(@RequestBody GifMergeRequest payload) {
        List<byte[]> images = getImages(payload.imageIds());
        return imageResult(withSemaphore(() -> ImageOps.gifMerge(images, payload.duration())));
    }

    @PostMapping("/image_operations/gif_reverse")
    public ImageResponse gifReverse(@RequestBody ImageRequestId payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.gifReverse(getImage(payload.imageId()))));
    }

    @PostMapping("/image_operations/gif_change_duration")
    public ImageResponse gifChangeDuration(@RequestBody GifDurationRequest payload) {
        return imageResult(withSemaphore(() ->
                ImageOps.gifChangeDuration(getImage(payload.imageId()), payload.duration())));
    }

    // ---------- helpers ----------

    private <T> T withSemaphore(java.util.function.Supplier<T> supplier) {
        try {
            semaphore.acquire();
            try {
                return supplier.get();
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private ImageResponse imageResult(byte[] data) {
        return new ImageResponse(tempFiles.create(data));
    }

    private byte[] getImage(String id) {
        byte[] data = tempFiles.get(id);
        if (data == null) {
            throw new GlobalExceptionHandler.TempIOException(
                    "temp file not found: " + id, null);
        }
        return data;
    }

    private List<byte[]> getImages(List<String> ids) {
        List<byte[]> out = new ArrayList<>();
        for (String id : ids) {
            out.add(getImage(id));
        }
        return out;
    }

    private static RenderMemeListParams emptyListParams() {
        return new RenderMemeListParams(Map.of(), List.of(), null, null, null, null);
    }

    private static RenderMemeListParams fillDefaults(RenderMemeListParams p) {
        return new RenderMemeListParams(
                p.memeProperties() == null ? Map.of() : p.memeProperties(),
                p.excludeMemes() == null ? List.of() : p.excludeMemes(),
                p.sortBy(),
                p.sortReverse(),
                p.textTemplate() == null ? "{index}. {keywords}" : p.textTemplate(),
                p.addCategoryIcon());
    }

    /** 简化版列表渲染：按模板逐行绘制（对齐原接口行为，样式后续迭代）。 */
    private byte[] renderListImage(RenderMemeListParams p) {
        List<Meme> memes = registry.getMemesSorted(
                MemeSortBy.fromValue(p.sortBy()), Boolean.TRUE.equals(p.sortReverse()));
        List<String> lines = new ArrayList<>();
        int index = 1;
        for (Meme meme : memes) {
            if (p.excludeMemes().contains(meme.key())) {
                continue;
            }
            String keywords = String.join("/", meme.info().keywords());
            String line = p.textTemplate()
                    .replace("{index}", String.valueOf(index))
                    .replace("{key}", meme.key())
                    .replace("{keywords}", keywords)
                    .replace("{shortcut}", "")
                    .replace("{tags}", String.join("/", meme.info().tags()));
            lines.add(line);
            index++;
        }
        int width = 900;
        float fontSize = 24f;
        float lineHeight = 36f;
        int height = (int) Math.max(200, lines.size() * lineHeight + 60);
        try (var surface = TextRenderer.newSurface(width, height)) {
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var font = meme.utils.FontManager.font("NotoSansSC-Regular.ttf", fontSize);
            var paint = new io.github.humbleui.skija.Paint().setColor(0xFF333333);
            float y = 40;
            for (String line : lines) {
                canvas.drawString(line, 30, y, font, paint);
                y += lineHeight;
            }
            paint.close();
            return meme.utils.ImageEdit.encodePng(surface.makeImageSnapshot());
        }
    }

    /** 简化版统计渲染。 */
    private byte[] renderStatisticsImage(RenderMemeStatisticsParams params) {
        int total = registry.getMemesSorted(MemeSortBy.KEY, false).size();
        String text = "Meme Statistics\nTotal memes: " + total;
        try (var surface = TextRenderer.newSurface(600, 240)) {
            var canvas = surface.getCanvas();
            canvas.clear(0xFFFFFFFF);
            var font = meme.utils.FontManager.font("NotoSansSC-Regular.ttf", 32f);
            var paint = new io.github.humbleui.skija.Paint().setColor(0xFF333333);
            int y = 90;
            for (String line : text.split("\n")) {
                canvas.drawString(line, 40, y, font, paint);
                y += 60;
            }
            paint.close();
            return meme.utils.ImageEdit.encodePng(surface.makeImageSnapshot());
        }
    }
}
