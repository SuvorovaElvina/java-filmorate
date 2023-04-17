package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Integer reviewId;
    @NotBlank(message = "Отзыв не может быть пустым.")
    private String content;
    @NotNull(message = "Отзыв должен содержать характеристику фильма")
    private Boolean isPositive;
    @NotNull(message = "Отзыв должен содержать ID пользователя.")
    private Integer userId;
    @NotNull(message = "Отзыв должен содержать ID фильма.")
    private Integer filmId;
    private Integer useful;

    public Review(String content, Boolean isPositive, Integer userId, Integer filmId) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }

    public Review(Integer reviewId, String content, Boolean isPositive, Integer userId, Integer filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }
}
