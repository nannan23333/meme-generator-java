package meme.server.controller;

import meme.core.Meme;
import meme.core.MemeError;
import meme.registry.MemeRegistryService;
import meme.registry.MemeSortBy;
import meme.server.TempFileService;
import meme.server.dto.ApiDtos.ErrorResponse;
import meme.server.dto.ApiDtos.ImageResponse;
import meme.server.dto.ApiDtos.ImageRef;
import meme.server.dto.ApiDtos.MemeRequest;
import meme.server.dto.ApiDtos.PreviewRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/** 表情元数据 / 搜索 / 生成端点。 */
@RestController
public class MemeController {

    private static final String VERSION = "0.1.0-java";

    private final MemeRegistryService registry;
    private final TempFileService tempFiles;
    private final Semaphore semaphore;

    public MemeController(MemeRegistryService registry, TempFileService tempFiles,
                          meme.server.MemeServerProperties props) {
        this.registry = registry;
        this.tempFiles = tempFiles;
        this.semaphore = new Semaphore(props.maxConcurrentTasks());
    }

    @GetMapping("/meme/version")
    public String version() {
        return VERSION;
    }

    @GetMapping("/meme/keys")
    public List<String> memeKeys(@RequestParam(name = "sort_by", required = false) String sortBy,
                                 @RequestParam(name = "sort_reverse", required = false) Boolean sortReverse) {
        return registry.getMemeKeysSorted(MemeSortBy.fromValue(sortBy),
                Boolean.TRUE.equals(sortReverse));
    }

    @GetMapping("/meme/infos")
    public List<?> memeInfos(@RequestParam(name = "sort_by", required = false) String sortBy,
                             @RequestParam(name = "sort_reverse", required = false) Boolean sortReverse) {
        return registry.getMemeInfosSorted(MemeSortBy.fromValue(sortBy),
                Boolean.TRUE.equals(sortReverse));
    }

    @GetMapping("/meme/search")
    public List<String> memeSearch(@RequestParam String query,
                                   @RequestParam(name = "include_tags", required = false) Boolean includeTags) {
        return registry.searchMemes(query, Boolean.TRUE.equals(includeTags));
    }

    @GetMapping("/memes/{key}/info")
    public ResponseEntity<?> memeInfo(@PathVariable String key) {
        Meme meme = registry.getMeme(key);
        if (meme == null) {
            return notFound();
        }
        return ResponseEntity.ok(meme.info());
    }

    @GetMapping("/memes/{key}/preview")
    public ResponseEntity<?> memePreviewGet(@PathVariable String key) {
        return generatePreview(key, Map.of());
    }

    @PostMapping("/memes/{key}/preview")
    public ResponseEntity<?> memePreview(@PathVariable String key,
                                         @RequestBody(required = false) PreviewRequest payload) {
        Map<String, Object> options = payload == null || payload.options() == null
                ? Map.of() : payload.options();
        return generatePreview(key, options);
    }

    @PostMapping("/memes/{key}")
    public ResponseEntity<?> memeGenerate(@PathVariable String key, @RequestBody MemeRequest payload) {
        Meme meme = registry.getMeme(key);
        if (meme == null) {
            return notFound();
        }
        List<meme.core.Image> images = new ArrayList<>();
        for (ImageRef ref : payload.images() == null ? List.<ImageRef>of() : payload.images()) {
            byte[] data = tempFiles.get(ref.id());
            if (data == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse(420, "IO error: temp file not found: " + ref.id(),
                                Map.of("error", "temp file not found: " + ref.id())));
            }
            images.add(new meme.core.Image(ref.name(), data));
        }
        List<String> texts = payload.texts() == null ? List.of() : payload.texts();
        Map<String, Object> options = payload.options() == null ? Map.of() : payload.options();

        byte[] result;
        try {
            semaphore.acquire();
            try {
                result = meme.generate(images, texts, options);
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new ImageResponse(tempFiles.create(result)));
    }

    private ResponseEntity<?> generatePreview(String key, Map<String, Object> options) {
        Meme meme = registry.getMeme(key);
        if (meme == null) {
            return notFound();
        }
        byte[] result;
        try {
            semaphore.acquire();
            try {
                result = meme.generatePreview(options);
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new ImageResponse(tempFiles.create(result)));
    }

    private static ResponseEntity<String> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meme not found");
    }
}
