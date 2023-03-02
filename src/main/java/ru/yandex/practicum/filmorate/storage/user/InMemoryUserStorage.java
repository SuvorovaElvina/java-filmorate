package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public void add(User user) {
        if (!users.containsKey(user.getId())) {
            user.setId(id++);
            users.put(user.getId(), user);
        }
    }

    @Override
    public void remove(Integer id) {
        users.remove(id);
    }

    @Override
    public void update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new RuntimeException("Такого пользователя нет в списке зарегистрированых.");
        } else {
            users.put(user.getId(), user);
        }
    }

    @Override
    public Map<Integer, User> getAll() {
        return this.users;
    }
}
