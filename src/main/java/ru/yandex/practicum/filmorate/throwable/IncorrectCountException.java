package ru.yandex.practicum.filmorate.throwable;

public class IncorrectCountException extends RuntimeException {
    public IncorrectCountException(String message) {
        super(message);
    }
}
