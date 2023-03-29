package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final LocalDate AFTER_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(int id) {
        if (filmStorage.getById(id) != null) {
            return filmStorage.getById(id);
        } else {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException("Фильм с указанный id - не существует.");
            }
        }
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmStorage.getById(filmId) != null) {
            if (userStorage.getById(userId) != null) {
                Film film = filmStorage.getById(filmId);
                film.getLikes().add(userId);
            } else {
                throw new NotFoundException("Пользователя с таким id - не существует.");
            }
        } else {
            throw new NotFoundException("Фильма с таким id - не существует");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmStorage.getById(filmId) != null) {
            if (userStorage.getById(userId) != null) {
                Film film = filmStorage.getById(filmId);
                if (film.getLikes().contains(userId)) {
                    film.getLikes().remove(userId);
                } else {
                    throw new IncorrectCountException("Этот пользователь не ставил лайк.");
                }
            } else {
                throw new NotFoundException("Пользователя с таким id - не существует.");
            }
        } else {
            throw new NotFoundException("Фильма с таким id - не существует");
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getAll();
        return films.stream()
                .sorted(Comparator.comparingInt(Film::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)) {
            throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                    + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
        }
    }
}