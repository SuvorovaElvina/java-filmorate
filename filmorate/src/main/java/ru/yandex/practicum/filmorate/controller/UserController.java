package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @GetMapping
    public ArrayList<User> users() {
        log.debug("Всего пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            if (!users.containsKey(user.getId())) {
                if (user.getName() == null || user.getName().isBlank()) {
                    user.setName(user.getLogin());
                }
                log.debug(String.valueOf(user));
                user.setId(id++);
                users.put(user.getId(), user);
            } else {
                throw new ValidationException("Этот пользователь уже зарегистрирован.");
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        try {
            if (users.containsKey(user.getId())) {
                if (user.getName() == null || user.getName().isBlank()) {
                    user.setName(user.getLogin());
                } else {
                    log.debug(String.valueOf(user));
                    users.put(user.getId(), user);
                }
            } else {
                throw new ValidationException("Такого пользователя нет в списке зарегистрированых.");
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
        return user;
    }
}
