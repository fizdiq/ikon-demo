package com.fizdiq.ikondemo.dto.fault;

import com.fizdiq.ikondemo.dto.handler.ResponseHandler;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@RestController
@Slf4j
public class CustomizedResponseEntityExceptionHandler {

    protected static final String ERROR = "ERROR";
    protected static final String BAD_REQUEST_ERROR_CODE = "904";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500",
            description = "Internal Server Error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHandler.class),
                    examples = @ExampleObject(value = """
                            {
                              "statusCode": "999",
                              "status": false,
                              "message": "GENERAL ERROR",
                              "data": null
                            }""")))
    public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        errorLogger(ex);
        ResponseHandler responseHandler = ResponseHandler.builder()
                .statusCode("999")
                .status(false)
                .message("GENERAL ERROR")
                .data(null)
                .build();
        return new ResponseEntity<>(responseHandler, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHandler.class),
                    examples = @ExampleObject(value = """
                            {
                              "statusCode": "904",
                              "status": false,
                              "message": "Validation Error: []",
                              "data": null
                            }""")))
    public final ResponseEntity<Object> handleValidationException(ValidationException ex) {
        errorLogger(ex);
        ResponseHandler responseHandler = ResponseHandler.builder()
                .statusCode(BAD_REQUEST_ERROR_CODE)
                .status(false)
                .message("Validation Error: Incorrect request body")
                .data(null)
                .build();
        return new ResponseEntity<>(responseHandler, BAD_REQUEST);

    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class

    })
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHandler.class),
                    examples = @ExampleObject(value = """
                            {
                              "statusCode": "904",
                              "status": false,
                              "message": "Validation Error: []",
                              "data": null
                            }""")))
    public final ResponseEntity<Object> handleMethodArgumentNotValid(Exception ex) {

        List<String> errors = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            for (final FieldError error : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            for (final ObjectError error : methodArgumentNotValidException.getBindingResult().getGlobalErrors()) {
                errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
            }
        }
        else if (ex instanceof ConstraintViolationException constraintViolationException) {
            errors = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .toList();

        }
        else if (ex instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
            List<String> missingParams = new ArrayList<>();
            if (missingServletRequestParameterException.getParameterName().equals("page")) {
                missingParams.add("page");
            }
            if (missingServletRequestParameterException.getParameterName().equals("size")) {
                missingParams.add("size");
            }

            String message = "Required request parameters are missing: " + missingParams;
            errors.add(message);
            log.error("Missing request parameters: {}", missingParams);

        }
        log.error("Validation failed at: {}", errors);
        ResponseHandler responseHandler = ResponseHandler.builder()
                .statusCode(BAD_REQUEST_ERROR_CODE)
                .status(false)
                .message(String.format("Validation Error: %s", errors))
                .data(null)
                .build();
        return new ResponseEntity<>(responseHandler, BAD_REQUEST);

    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class
    })
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHandler.class),
                    examples = @ExampleObject(value = """
                            {
                              "statusCode": "904",
                              "status": false,
                              "message": "Validation Error: []",
                              "data": null
                            }""")))
    public final ResponseEntity<Object> handleHttpMessageNotReadable(Exception ex) {
        errorLogger(ex);
        ResponseHandler responseHandler = ResponseHandler.builder()
                .statusCode(BAD_REQUEST_ERROR_CODE)
                .status(false)
                .message("Validation Error: " +
                        (ex instanceof HttpMediaTypeNotSupportedException
                                ? ex.getMessage()
                                : "JSON parse error"))
                .data(null)
                .build();
        return new ResponseEntity<>(responseHandler, BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ApiResponse(responseCode = "405",
            description = "Method Not Allowed",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHandler.class),
                    examples = @ExampleObject(value = """
                            {
                              "statusCode": "905",
                              "status": "ERROR",
                              "message": "Method Error: []",
                              "data": null
                            }""")))
    public final ResponseEntity<Object> handleHttpRequestMethodNotSupported(Exception ex) {
        errorLogger(ex);
        ResponseHandler responseHandler = ResponseHandler.builder()
                .statusCode("905")
                .status(false)
                .message(String.format("Method Error: %s", ex.getMessage()))
                .data(null)
                .build();
        return new ResponseEntity<>(responseHandler, METHOD_NOT_ALLOWED);

    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ApiResponse(responseCode = "404",
            description = "Path Not Found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseHandler.class),
                    examples = @ExampleObject(value = """
                            {
                              "statusCode": "906",
                              "status": "ERROR",
                              "message": "No Resource Error: Path not found",
                              "data": null
                            }""")))
    public final ResponseEntity<Object> handleNoResourceFound(Exception ex) {
        errorLogger(ex);
        ResponseHandler responseHandler = ResponseHandler.builder()
                .statusCode("906")
                .status(false)
                .message("No Resource Error: Path not found")
                .data(null)
                .build();
        return new ResponseEntity<>(responseHandler, NOT_FOUND);
    }

    private static void errorLogger(Exception ex) {
        log.error("ERROR: {} at {}", ex, ex.getStackTrace()[0].toString());
    }
}
