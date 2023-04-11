package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Review {

    private Integer id;
    @NotBlank(message = "Отзыв не может быть пустым.")
    private final String content;
    private final boolean isPositive;
    @NotNull(message = "Отзыв дожен содержать ID пользователя.")
    private final Integer userId;
    @NotNull(message = "Отзыв дожен содержать ID фильма.")
    private final Integer filmId;
    private Integer useful;

}
