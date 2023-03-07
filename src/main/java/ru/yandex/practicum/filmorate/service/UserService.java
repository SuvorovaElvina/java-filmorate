package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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
            validateId(id);
        }
        return userStorage.getById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        if (user != null) {
            if (friend != null) {
                user.getFriends().add(friendId);
                friend.getFriends().add(userId);
            } else {
                validateId(friendId);
            }
        } else {
            validateId(userId);
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        if (user != null) {
            if (friend != null) {
                user.getFriends().remove(friendId);
                friend.getFriends().remove(userId);
            } else {
                validateId(friendId);
            }
        } else {
            validateId(userId);
        }
    }

    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getById(userId) != null) {
            for (Integer friendsId : userStorage.getById(userId).getFriends()) {
                friends.add(userStorage.getById(friendsId));
            }
        } else {
            validateId(userId);
        }
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        if (userStorage.getById(userId) == null & userStorage.getById(otherId) == null) {
            validateId(userId);
            validateId(otherId);
        }
        for (Integer friendsId : userStorage.getById(userId).getFriends()) {
            if (userStorage.getById(otherId).getFriends().contains(friendsId)) {
                commonFriends.add(userStorage.getById(friendsId));
            }
        }
        return commonFriends;
    }

    private void validateId(int id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else {
            throw new IncorrectCountException("Пользователя с указанным id - не существует.");
        }
    }
}