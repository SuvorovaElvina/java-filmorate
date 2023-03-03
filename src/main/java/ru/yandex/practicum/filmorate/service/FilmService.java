package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final LocalDate AFTER_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void createFilm(Film film) {
        validateFilm(film);
        filmStorage.add(film);
    }

    public void updateFilm(Film film) {
        validateFilm(film);
        filmStorage.update(film);
    }

    public Map<Integer, Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(int id) {
        if (filmStorage.getAll().containsKey(id)) {
            return filmStorage.getAll().get(id);
        } else {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new IncorrectCountException("Фильм с указанный id - не существует.");
            }
        }
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmStorage.getAll().containsKey(filmId)) {
            if (userStorage.getAll().containsKey(userId)) {
                Film film = filmStorage.getAll().get(filmId);
                film.getLikes().add(userId);
            } else {
                throw new IncorrectCountException("Пользователя с таким id - не существует.");
            }
        } else {
            throw new IncorrectCountException("Фильма с таким id - не существует");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmStorage.getAll().containsKey(filmId)) {
            if (userStorage.getAll().containsKey(userId)) {
                Film film = filmStorage.getAll().get(filmId);
                film.getLikes().remove(userId);
            } else {
                throw new IncorrectCountException("Пользователя с таким id - не существует.");
            }
        } else {
            throw new IncorrectCountException("Фильма с таким id - не существует");
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = new ArrayList<>(filmStorage.getAll().values());
        List<Film> sortedFilms = films.stream()
                .sorted(Comparator.comparingInt(Film::getCountLikes).reversed())
                .collect(Collectors.toList());
        if (sortedFilms.size() > count) {
            return sortedFilms.subList(0, count);
        } else {
            return sortedFilms;
        }
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)) {
            throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                    + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
        }
    }
}
