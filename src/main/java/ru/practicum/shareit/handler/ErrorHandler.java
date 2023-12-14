package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UserCreationException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({ItemNotExistException.class, UserNotExistException.class, TimeValidationException.class,
            AvailabilityException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notFoundException(final Exception e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserCreationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse userCreationException(final Exception e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, NoSuchElementException.class, ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundException(final Exception e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
