package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Review {

    private Integer reviewId;
    @NotBlank(message = "Отзыв не может быть пустым.")
    private String content;
    private Boolean isPositive;
    @NotNull(message = "Отзыв должен содержать ID пользователя.")
    private Integer userId;
    @NotNull(message = "Отзыв должен содержать ID фильма.")
    private Integer filmId;
    private Integer useful;

}
