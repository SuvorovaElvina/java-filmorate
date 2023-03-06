package ru.yandex.practicum.filmorate.throwable;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
