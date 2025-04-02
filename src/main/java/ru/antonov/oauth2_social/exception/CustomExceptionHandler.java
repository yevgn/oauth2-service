package ru.antonov.oauth2_social.exception;

import lombok.extern.log4j.Log4j2;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
@Log4j2
public class CustomExceptionHandler{

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedEx(UnauthorizedException ex, WebRequest request) throws Exception {
        log.error("Error: ", ex);
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errors(Arrays.asList(ex.getMessage()))
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ ClientNotFoundException.class, TokenConfigurationException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, NoResourceFoundException.class, UserNotFoundException.class} )
    public ResponseEntity<ApiError> handle4xxExceptions(Exception ex, WebRequest request) throws Exception {
        log.error("Error: ", ex);
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(Arrays.asList(ex.getMessage()))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationEx(ConstraintViolationException ex){
        ApiError apiError = ApiError
                .builder()
                .status(HttpStatus.CONFLICT)
                .errors(List.of(ex.getSQLException().getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Error: ", ex);
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );

        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidationEx(HandlerMethodValidationException ex) {
        log.error("Error: ", ex);
        List<String> errors = new ArrayList<>();

        ex.getParameterValidationResults().forEach(validationRes ->
                validationRes.getResolvableErrors().forEach( error ->
                        errors.add(error.getDefaultMessage())
                ));

        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }


}
