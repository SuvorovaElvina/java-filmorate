package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("filmDbStorage")
    private final FilmStorage filmsStorage;

    @Qualifier("feedDbStorage")
    private final FeedStorage feedStorage;

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
        Optional<User> userOptional = userStorage.update(user);
        return userOptional.orElseThrow(() -> new NotFoundException(String.format("Пользователя с id - %d нет в списке зарегистрированых.", user.getId())));
    }

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUser(int id) {
        Optional<User> userOpt = userStorage.getById(id);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException(String.format("Пользователя с id %d - не существует.", id));
            }
        }
    }

    public void removeUser(int id) {
        validate(id);
        userStorage.remove(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        validate(userId);
        validate(friendId);
        userStorage.addFriend(userId, friendId);
        feedStorage.createFeed(userId, "FRIEND", "ADD", friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        validate(userId);
        validate(friendId);
        userStorage.removeFriend(userId, friendId);
        feedStorage.createFeed(userId, "FRIEND", "REMOVE", friendId);
    }

    public List<User> getFriends(Integer userId) {
        validate(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        validate(userId);
        validate(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<Film> getRecommendations(int userId) {
        User user = getUser(userId);
        List<Film> films = filmsStorage.getAll();
        List<Film> userFilms = filmsStorage.getLikes(userId, films);
        List<User> users = userStorage.getAll();
        Map<User, HashMap<Film, Double>> inputData = filmsStorage.getRecommendationData(users, films);
        inputData = SlopeOne.slopeOne(inputData, films);
        HashMap<Film, Double> userRatings = inputData.get(user);
        films = new ArrayList<>();
        if (userRatings == null) return films;
        for (Film film : userRatings.keySet()) {
            if ((userRatings.get(film) > 0) && (!userFilms.contains(film)))
                films.add(film);
        }
        return films;
    }

    public List<Feed> getUserFeed(Integer id) {
        return userStorage.getUserFeed(id);
    }

    private void validate(Integer id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else if (userStorage.getById(id).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id %d - не существует.", id));
        }
    }
}