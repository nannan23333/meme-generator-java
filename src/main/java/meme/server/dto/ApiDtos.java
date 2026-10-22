package meme.server.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** API DTO 集合，字段与 Rust serde 序列化完全对齐（snake_case）。 */
public final class ApiDtos {

    private ApiDtos() {
    }

    // ---------- 图片上传 ----------

    /**
     * 上传图片请求，对齐 Rust ImageData：
     * {"type":"url","url":...,"headers":{...}} / {"type":"path","path":...} / {"type":"data","data":base64}
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImageData.UrlData.class, name = "url"),
            @JsonSubTypes.Type(value = ImageData.PathData.class, name = "path"),
            @JsonSubTypes.Type(value = ImageData.DataData.class, name = "data"),
    })
    public sealed interface ImageData {
        record UrlData(String url, Map<String, String> headers) implements ImageData {
        }

        record PathData(String path) implements ImageData {
        }

        record DataData(byte[] data) implements ImageData {
        }
    }

    public record UploadImageResponse(@JsonProperty("image_id") String imageId) {
    }

    // ---------- 表情生成 ----------

    public record ImageRef(String name, String id) {
    }

    public record MemeRequest(List<ImageRef> images, List<String> texts,
                              Map<String, Object> options) {
    }

    public record ImageResponse(@JsonProperty("image_id") String imageId) {
    }

    public record ImagesResponse(@JsonProperty("image_ids") List<String> imageIds) {
    }

    public record PreviewRequest(Map<String, Object> options) {
    }

    // ---------- 图像操作 ----------

    public record ImageRequestId(@JsonProperty("image_id") String imageId) {
    }

    public record RotateRequest(@JsonProperty("image_id") String imageId, Float degrees) {
    }

    public record ResizeRequest(@JsonProperty("image_id") String imageId, Integer width, Integer height) {
    }

    public record CropRequest(@JsonProperty("image_id") String imageId,
                              Integer left, Integer top, Integer right, Integer bottom) {
    }

    public record ImagesRequest(@JsonProperty("image_ids") List<String> imageIds) {
    }

    public record GifMergeRequest(@JsonProperty("image_ids") List<String> imageIds, Float duration) {
    }

    public record GifDurationRequest(@JsonProperty("image_id") String imageId, Float duration) {
    }

    // ---------- 错误 ----------

    public record ErrorResponse(int code, String message, Object data) {
    }

    // ---------- render_list / render_statistics ----------

    public record MemeProperties(Map<String, Object> properties) {
    }

    public record RenderMemeListParams(
            @JsonProperty("meme_properties") Map<String, Map<String, Object>> memeProperties,
            @JsonProperty("exclude_memes") List<String> excludeMemes,
            @JsonProperty("sort_by") String sortBy,
            @JsonProperty("sort_reverse") Boolean sortReverse,
            @JsonProperty("text_template") String textTemplate,
            @JsonProperty("add_category_icon") Boolean addCategoryIcon) {
    }

    public record RenderMemeStatisticsParams(
            @JsonProperty("meme_list") Map<String, List<Object>> memeList,
            @JsonProperty("statistics_type") String statisticsType,
            String date) {
    }
}
