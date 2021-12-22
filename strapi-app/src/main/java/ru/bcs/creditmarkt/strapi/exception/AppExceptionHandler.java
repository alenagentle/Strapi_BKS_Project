package ru.bcs.creditmarkt.strapi.exception;

import com.poiji.exception.HeaderMissingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler({FileFormatException.class})
    protected ResponseEntity<Object> handleNotFoundException(FileFormatException e) {
        ApiError apiError = new ApiError(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HeaderMissingException.class})
    protected ResponseEntity<Object> handleHeaderMissingException(HeaderMissingException e) {
        ApiError apiError = new ApiError(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        ApiError apiError = new ApiError(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleNotFoundException(ConstraintViolationException e) {
        ApiError apiError = new ApiError(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SizeLimitExceededException.class})
    protected ResponseEntity<Object> handleNotFoundException(SizeLimitExceededException e) {
        ApiError apiError = new ApiError(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


}
