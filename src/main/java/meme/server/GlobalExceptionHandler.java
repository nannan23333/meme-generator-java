package meme.server;

import meme.core.Meme;
import meme.core.MemeError;
import meme.server.dto.ApiDtos.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：对齐 Rust ErrorResponse（HTTP 500 + {code, message, data}）。
 * 下载/IO 错误码：410 请求 / 420 IO。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MemeError.class)
    public ResponseEntity<ErrorResponse> handleMemeError(MemeError e) {
        log.warn("Meme error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getCode(), e.getMessage(), e.getData()));
    }

    @ExceptionHandler(ResourceDownloadException.class)
    public ResponseEntity<ErrorResponse> handleDownload(ResourceDownloadException e) {
        log.warn("Server error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(410, "Request error: " + e.getMessage(),
                        java.util.Map.of("error", String.valueOf(e.getMessage()))));
    }

    @ExceptionHandler(TempIOException.class)
    public ResponseEntity<ErrorResponse> handleIo(TempIOException e) {
        log.warn("Server error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(420, "IO error: " + e.getMessage(),
                        java.util.Map.of("error", String.valueOf(e.getMessage()))));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, String.valueOf(e.getMessage()), java.util.Map.of()));
    }

    public static class ResourceDownloadException extends RuntimeException {
        public ResourceDownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class TempIOException extends RuntimeException {
        public TempIOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
