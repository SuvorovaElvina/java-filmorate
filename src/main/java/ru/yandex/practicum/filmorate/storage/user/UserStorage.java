package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    void remove(Integer id);

    Optional<User> update(User user);

    List<User> getAll();

    Optional<User> getById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);

    void removeFriend(Integer userId, Integer friendId);

    void createFeed(int userId, String eventType, String operation, int entityId);

    List<String> getUserFeed(Integer id);
}
