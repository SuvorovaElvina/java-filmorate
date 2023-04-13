package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@Builder
//@RequiredArgsConstructor
public class Review {

    private Integer id;
    @NotBlank(message = "Отзыв не может быть пустым.")
    private final String content;
    private final boolean isPositive;
    @NotNull(message = "Отзыв должен содержать ID пользователя.")
    private final Integer userId;
    @NotNull(message = "Отзыв должен содержать ID фильма.")
    private final Integer filmId;
    private Integer useful;

    public Review(Integer id, String content, boolean isPositive, Integer userId, Integer filmId) {
        this.id = id;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }
}
