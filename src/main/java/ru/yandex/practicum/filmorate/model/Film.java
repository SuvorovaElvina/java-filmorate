package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;
    @NotNull(message = "Описание должно быть заполнено.")
    @Size(max = 200, message = "Нельзя описание превышать за 200 символов")
    String description;
    @NotNull(message = "Дата релиза должна быть заполнена.")
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    Long duration;
    @NotNull(message = "Рейтинг фильма не может быть пустым.")
    Mpa mpa;
    List<Genre> genres = new ArrayList<>();
    List<Director> directors = new ArrayList<>();

    public Film(String name, String description, LocalDate releaseDate, Long duration, Mpa mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
