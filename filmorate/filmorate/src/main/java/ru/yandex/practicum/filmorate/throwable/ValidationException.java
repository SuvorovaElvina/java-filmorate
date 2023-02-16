package ru.yandex.practicum.filmorate.throwable;
//проверить
public class ValidationException extends Error {

    public ValidationException(String message) {
        super(message);
    }
}
