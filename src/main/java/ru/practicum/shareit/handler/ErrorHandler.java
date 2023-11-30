package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserCreationException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({ItemNotExistException.class, UserNotExistException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> notFoundException(final Exception e) {
        log.warn(e.getMessage(), e);
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler({UserCreationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> userCreationException(final Exception e) {
        log.warn(e.getMessage(), e);
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> userNotFoundException(final Exception e) {
        log.warn(e.getMessage(), e);
        return Map.of("Error", e.getMessage());
    }
}
