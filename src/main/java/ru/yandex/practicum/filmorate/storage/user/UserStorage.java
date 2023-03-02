package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    void add(User user);

    void remove(Integer id);

    void update(User user);

    Map<Integer, User> getAll();
}
