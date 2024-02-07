package com.ssafy.websockettest.common.exception;

import com.ssafy.websockettest.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.ssafy.websockettest.common.response.BaseResponseStatus.*;

@Slf4j
@EnableWebMvc
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * Bad Request Error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public BaseErrorResponse handleBadRequest(Exception e) {
        log.error("[BadRequestException]", e);
        return BaseErrorResponse.of(BAD_REQUEST);
    }

    /**
     * URL Not Found Error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoHandlerFoundException.class, TypeMismatchException.class})
    public BaseErrorResponse handleUrlNotFoundException(Exception e) {
        log.error("[UrlNotFoundException]", e);
        return BaseErrorResponse.of(URL_NOT_FOUND);
    }

    /**
     * Method Not Supported Error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("[HttpRequestMethodNotSupportedException]", e);
        return BaseErrorResponse.of(METHOD_NOT_SUPPORTED);
    }

    /**
     * Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public BaseErrorResponse handleRuntimeException(Exception e) {
        log.error("[RuntimeException]", e);
        return BaseErrorResponse.of(SERVER_ERROR);
    }

    /**
     * Validation Error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseErrorResponse handleConstraintViolationException(Exception e) {
        log.error("[ConstraintViolationException]", e);
        return BaseErrorResponse.of(BAD_REQUEST, e.getMessage());
    }

    /**
     * Custom Bad Request
     */
    @Priority(0)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    protected BaseErrorResponse handleCustomBadRequestException(CustomBadRequestException e) {
        log.error("[CustomBadRequestException]");
        return BaseErrorResponse.of(e.getBaseResponseStatus());
    }

    /**
     * Custom Internal Server Error
     */
    @Priority(0)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomServerErrorException.class)
    public BaseErrorResponse handleCustomServerErrorException(CustomServerErrorException e) {
        log.error("[CustomServerErrorException]", e);
        return BaseErrorResponse.of(e.getBaseResponseStatus());
    }

    /**
     * Custom Unauthorized Error
     */
    @Priority(0)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CustomUnauthorizedException.class)
    public BaseErrorResponse handleCustomUnauthorizedException(CustomUnauthorizedException e) {
        log.error("[CustomUnauthorizedException]", e);
        return BaseErrorResponse.of(e.getBaseResponseStatus());
    }

}
