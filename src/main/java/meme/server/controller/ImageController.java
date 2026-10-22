package meme.server.controller;

import meme.server.GlobalExceptionHandler.ResourceDownloadException;
import meme.server.GlobalExceptionHandler.TempIOException;
import meme.server.TempFileService;
import meme.server.dto.ApiDtos.ImageData;
import meme.server.dto.ApiDtos.UploadImageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** 图片上传 / 取回端点。 */
@RestController
public class ImageController {

    private final TempFileService tempFiles;

    public ImageController(TempFileService tempFiles) {
        this.tempFiles = tempFiles;
    }

    @PostMapping("/image/upload")
    public UploadImageResponse upload(@RequestBody ImageData imageData) {
        byte[] data = switch (imageData) {
            case ImageData.UrlData(String url, Map<String, String> headers) -> {
                try {
                    yield download(url, headers);
                } catch (Exception e) {
                    throw new ResourceDownloadException(e.getMessage(), e);
                }
            }
            case ImageData.PathData(String path) -> {
                try {
                    yield Files.readAllBytes(Path.of(path));
                } catch (IOException e) {
                    throw new TempIOException(e.getMessage(), e);
                }
            }
            case ImageData.DataData(byte[] bytes) -> bytes;
        };
        return new UploadImageResponse(tempFiles.create(data));
    }

    @PostMapping("/image/upload/multipart")
    public ResponseEntity<?> uploadMultipart(@RequestParam("file") MultipartFile file) {
        try {
            byte[] data = file.getBytes();
            return ResponseEntity.ok(new UploadImageResponse(tempFiles.create(data)));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The field 'file' is required");
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImage(@PathVariable String id) {
        byte[] data = tempFiles.get(id);
        if (data == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new meme.server.dto.ApiDtos.ErrorResponse(420,
                            "IO error: temp file not found: " + id,
                            Map.of("error", "temp file not found: " + id)));
        }
        String mime = detectMime(data);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime)).body(data);
    }

    private static byte[] download(String url, Map<String, String> headers) throws Exception {
        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET();
        if (headers != null) {
            headers.forEach(builder::header);
        }
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpResponse<byte[]> response =
                client.send(builder.build(), java.net.http.HttpResponse.BodyHandlers.ofByteArray());
        return response.body();
    }

    /** 魔数嗅探 Content-Type，对齐 Rust infer crate。 */
    static String detectMime(byte[] data) {
        if (data.length >= 8 && (data[0] & 0xFF) == 0x89 && data[1] == 'P' && data[2] == 'N'
                && data[3] == 'G') {
            return "image/png";
        }
        if (data.length >= 3 && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8) {
            return "image/jpeg";
        }
        if (data.length >= 4 && data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            return "image/gif";
        }
        if (data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F'
                && data[3] == 'F' && data[8] == 'W' && data[9] == 'E' && data[10] == 'B') {
            return "image/webp";
        }
        if (data.length >= 2 && data[0] == 'B' && data[1] == 'M') {
            return "image/bmp";
        }
        return "application/octet-stream";
    }
}
