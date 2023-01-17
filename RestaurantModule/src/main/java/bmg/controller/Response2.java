package bmg.controller;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an HTTP response object
 */
public class Response2<T> {
    private LocalDateTime timestamp;
    private Integer status;
    private T data;
    private String message;
    private String path;

    public Response2() {
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Response2<?> response))
            return false;

        return Objects.equals(timestamp, response.timestamp)
                && Objects.equals(status, response.status)
                && Objects.equals(data, response.data)
                && Objects.equals(message, response.message)
                && Objects.equals(path, response.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, status, data, message, path);
    }

    @Override
    public String toString() {
        return "Response{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
