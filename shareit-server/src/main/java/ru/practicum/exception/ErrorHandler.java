package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternal(Throwable e) {
        return new ErrorResponse("Internal server error");
    }
}

/*
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        log.error("Not found error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException ex) {
        log.error("Forbidden error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UnavailableItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItem(UnavailableItemException ex) {
        log.error("Unavailable item error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AlreadyProcessedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyProcessed(AlreadyProcessedException ex) {
        log.error("Already processed error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation error: {}", errorMessage);
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Internal server error", ex);
        return new ErrorResponse("Internal server error. Please contact support.");
    }
}

/*package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        log.error("Not found error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException ex) {
        log.error("Forbidden error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UnavailableItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItem(UnavailableItemException ex) {
        log.error("Unavailable item error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AlreadyProcessedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyProcessed(AlreadyProcessedException ex) {
        log.error("Already processed error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation error: {}", errorMessage);
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Internal server error", ex);
        return new ErrorResponse("Internal server error. Please contact support.");
    }
}

/*
package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        log.error("Not found error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Internal server error", ex);
        return new ErrorResponse("Internal server error. Please contact support.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    //новое ниже
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return new ErrorResponse("Validation error: " + ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", ")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Throwable exception) {
        return new ErrorResponse("An unexpected error occurred.");
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
*/