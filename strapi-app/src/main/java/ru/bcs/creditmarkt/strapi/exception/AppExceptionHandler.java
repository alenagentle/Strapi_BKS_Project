package ru.bcs.creditmarkt.strapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler({FileFormatException.class})
    protected ResponseEntity<Object> handleNotFoundException(FileFormatException e) {
        ApiError apiError = new ApiError(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
