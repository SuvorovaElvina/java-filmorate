package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Integer reviewId;
    @NotBlank(message = "Отзыв не может быть пустым.")
    String content;
    @NotNull(message = "Отзыв должен содержать характеристику фильма")
    Boolean isPositive;
    @NotNull(message = "Отзыв должен содержать ID пользователя.")
    Integer userId;
    @NotNull(message = "Отзыв должен содержать ID фильма.")
    Integer filmId;
    Integer useful;

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
