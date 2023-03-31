package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUser(int id) {
        if (userStorage.getById(id) == null) {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new IncorrectCountException("Пользователя с указанным id - не существует.");
            }
        }
        return userStorage.getById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        getUser(userId);
        getUser(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }
}