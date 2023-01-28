package bmg.controller;

import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.NoSuchElementException;

// TODO: move all exception handling to use AOP (aspect oriented programming)
/**
 * A base class for all Controllers
 *
 * @param <T> The controlled data type
 */
public abstract class Controller<T> {

    /**
     * Handles NoSuchElementExceptions
     *
     * @param e A NoSuchElementException
     * @return A 404 Not Found response entity
     */
    @ExceptionHandler({NoSuchElementException.class})
    private ResponseEntity<Response<T>> handleException(NoSuchElementException e) {
        return responseCodeNotFound(e.getMessage());
    }

    /**
     * Handles HttpMessageNotReadableExceptions
     *
     * @param e An HttpMessageNotReadableException
     * @return A 400 Bad Request response entity
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    private ResponseEntity<Response<T>> handleException(HttpMessageNotReadableException e) {
        return responseCodeBadRequest(e.getMessage());
    }

    /**
     * Handles ValidationExceptions
     *
     * @param e A ValidationException
     * @return A 400 Bad Request response entity
     */
    @ExceptionHandler({ValidationException.class})
    private ResponseEntity<Response<T>> handleException(ValidationException e) {
        return responseCodeBadRequest(e.getMessage());
    }

    /**
     * Handles MethodArgumentNotValidExceptions
     *
     * @param e A MethodArgumentNotValidException
     * @return A 400 Bad Request response entity
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    private ResponseEntity<Response<T>> handleException(MethodArgumentNotValidException e) {
        return responseCodeBadRequest(e.getMessage());
    }

    /**
     * Handles IllegalArgumentException
     *
     * @param e An IllegalArgumentException
     * @return A 400 Bad Request response entity
     */
    @ExceptionHandler({IllegalArgumentException.class})
    private ResponseEntity<Response<T>> handleException(IllegalArgumentException e) {
        return responseCodeBadRequest(e.getMessage());
    }

    /**
     * Handles MissingServletRequestParameterException
     *
     * @param e An MissingServletRequestParameterException
     * @return A 400 Bad Request response entity
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    private ResponseEntity<Response<T>> handleException(MissingServletRequestParameterException e) {
        return responseCodeBadRequest(e.getMessage());
    }

    /**
     * Handles TransactionCanceledException
     *
     * @param e A TransactionCanceledException
     * @return A 409 Conflict response entity
     */
    @ExceptionHandler({TransactionCanceledException.class})
    private ResponseEntity<Response<T>> handleException(TransactionCanceledException e) {
        return responseCodeConflict(e.getMessage());
    }

    /**
     * Creates a response entity indicating that the request was successful
     *
     * @param data A list of data to send in the response body
     * @return A 200 OK response entity
     */
    public ResponseEntity<Response<T>> responseCodeOk(List<T> data) {
        HttpStatus status = HttpStatus.OK;
        return responseEntity(status, status.name(), data, null);
    }

    /**
     * Creates a response entity indicating that a new resource has been created
     *
     * @param data A list of data to send in the response body
     * @param pathId The URI path id of a new resource
     * @return A 201 Created response entity with a location header
     */
    public ResponseEntity<Response<T>> responseCodeCreated(List<T> data, String pathId) {
        HttpStatus status = HttpStatus.CREATED;
        return responseEntity(
                status,
                status.name(),
                data,
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .toUriString()
                        +pathId
        );
    }

    /**
     * Creates a response entity indicating that there is no content to return
     *
     * @return A 204 No Content response entity
     */
    public ResponseEntity<Response<T>> responseCodeNoContent() {
        HttpStatus status = HttpStatus.NO_CONTENT;
        return responseEntity(status, status.name(), null, null);
    }

    /**
     * Creates a response entity indicating a bad request
     *
     * @param message An error message
     * @return A 400 Bad Request response entity
     */
    public ResponseEntity<Response<T>> responseCodeBadRequest(String message) {
        return responseEntity(HttpStatus.BAD_REQUEST, message, null, null);
    }

    /**
     * Creates a response entity indicating that the requested resource does not exist
     *
     * @param message An error message
     * @return A 404 Not Found response entity
     */
    public ResponseEntity<Response<T>> responseCodeNotFound(String message) {
        return responseEntity(HttpStatus.NOT_FOUND, message, null, null);
    }

    /**
     * Creates a response entity indicating that the request is in conflict with
     * the current state of the server
     *
     * @param message An error message
     * @return A 409 Conflict response entity
     */
    public ResponseEntity<Response<T>> responseCodeConflict(String message) {
        return responseEntity(HttpStatus.CONFLICT, message, null, null);
    }

    /**
     * Creates a response entity
     *
     * @param status An HTTP status
     * @param message A response message
     * @param data A list of data to send in the response body
     * @param pathId The URI path id of a new or updated resource
     * @return A response entity
     */
    private ResponseEntity<Response<T>> responseEntity(HttpStatus status, String message,
                                                       List<T> data, String pathId) {
        if (pathId != null) {
            return ResponseEntity
                    .status(status)
                    .header("Location", pathId)
                    .body(response(status, message, data));
        }
        return ResponseEntity
                .status(status)
                .body(response(status, message, data));
    }

    /**
     * Creates a response
     *
     * @param status An HTTP status
     * @param message A response message
     * @param data A list of data to send in the response body
     * @return A response
     */
    private Response<T> response(HttpStatus status, String message, List<T> data) {
        return Response
                .<T>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();
    }
}
