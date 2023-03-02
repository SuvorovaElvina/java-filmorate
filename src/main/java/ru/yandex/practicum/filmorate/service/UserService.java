package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void createUser(User user) {
        userStorage.add(user);
    }

    public void updateUser(User user) {
        userStorage.update(user);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getAll();
    }

    public User getUser(int id) {
        if (!userStorage.getAll().containsKey(id)) {
            validateId(id);
        }
        return userStorage.getAll().get(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.getAll().containsKey(userId)) {
            validateId(userId);
        } else if (!userStorage.getAll().containsKey(friendId)) {
            validateId(friendId);
        }
        User user = userStorage.getAll().get(userId);
        User friend = userStorage.getAll().get(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        if (!userStorage.getAll().containsKey(userId)) {
            validateId(userId);
        } else if (!userStorage.getAll().containsKey(friendId)) {
            validateId(friendId);
        }
        User user = userStorage.getAll().get(userId);
        User friend = userStorage.getAll().get(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getAll().containsKey(userId)) {
            for (Integer friendsId : userStorage.getAll().get(userId).getFriends()) {
                friends.add(userStorage.getAll().get(friendsId));
            }
        } else {
            validateId(userId);
        }
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        if (!userStorage.getAll().containsKey(userId) & !userStorage.getAll().containsKey(otherId)) {
            validateId(userId);
            validateId(otherId);
        }
        for (Integer friendsId : userStorage.getAll().get(userId).getFriends()) {
            if (userStorage.getAll().get(otherId).getFriends().contains(friendsId)) {
                commonFriends.add(userStorage.getAll().get(friendsId));
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
