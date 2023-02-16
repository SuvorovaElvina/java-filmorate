package ru.yandex.practicum.filmorate.throwable;
//проверить
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
