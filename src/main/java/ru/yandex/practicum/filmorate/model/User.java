package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    private Integer id;
    @NotBlank(message = "Email не должно быть пустым.")
    @Email(message = "Введён не email.")
    private String email;
    @NotBlank(message = "Логин не должно быть пустым.")
    private String login;
    private String name;
    @Past(message = "День рождения не должно быть в будущем.")
    private LocalDate birthday;
}
