package bmg.controller;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an HTTP response object
 *
 * @param <T> The data type involved in the HTTP request
 */
@Getter
@Builder
public class Response<T> {
    @Builder.Default
    private final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private Integer status;
    private List<T> data;
    private String message;
    private String path;
}
