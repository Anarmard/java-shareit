package ru.practicum.shareitserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    // 400 — если ошибка валидации: ValidationException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final ValidationException e) {
        log.info("400 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    // 404 — для всех ситуаций, если искомый объект не найден
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NotFoundException e) {
        log.info("404 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    // 409 — если объект уже существует
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse alreadyExists(AlreadyExistsException e) {
        log.info("409 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
