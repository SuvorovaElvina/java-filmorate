package ru.yandex.practicum.filmorate.throwable;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
