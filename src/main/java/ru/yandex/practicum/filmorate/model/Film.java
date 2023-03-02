package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;
    @NotNull(message = "Описание должно быть заполнено.")
    @Size(max = 200, message = "Нельзя описание превышать за 200 символов")
    private String description;
    @NotNull(message = "Дата релиза должна быть заполнена.")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private Long duration;
    private Set<Integer> likes = new HashSet<>();

    public Integer getCountLikes() {
        return likes.size();
    }
}
