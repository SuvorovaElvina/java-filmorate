package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "Email не должно быть пустым.")
    @Email(message = "Введён не email.")
    private String email;
    @NotBlank(message = "Логин не должно быть пустым.")
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String login;
    private String name;
    @Past(message = "День рождения не должно быть в будущем.")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();


    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
