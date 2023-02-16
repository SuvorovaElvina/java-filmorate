package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Long duration;
}
