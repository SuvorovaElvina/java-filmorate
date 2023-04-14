package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate AFTER_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        Optional<Film> filmOptional = filmStorage.update(film);
        return filmOptional.orElseThrow(() -> new NotFoundException("Такого фильма нет в списке зарегистрированных."));
    }

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(int id) {
        Optional<Film> filmOpt = filmStorage.getById(id);
        if (filmOpt.isPresent()) {
            return filmOpt.get();
        } else {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException("Фильм с указанный id - не существует.");
            }
        }
    }

    public void removeFilm(int id) {
        Optional<Film> filmOpt = filmStorage.getById(id);
        if (filmOpt.isEmpty()) {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException("Фильм с указанный id - не существует.");
            }
        }
        filmStorage.remove(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmStorage.getById(filmId).isPresent()) {
            if (userStorage.getById(userId).isPresent()) {
                filmStorage.addLike(filmId, userId);
            } else {
                throw new NotFoundException("Пользователя с таким id - не существует.");
            }
        } else {
            throw new NotFoundException("Фильма с таким id - не существует");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        Optional<Film> filmOpt = filmStorage.getById(filmId);
        Optional<User> userOpt = userStorage.getById(userId);
        if (filmOpt.isEmpty()) {
            throw new NotFoundException("Фильма с таким id - не существует");
        }
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id - не существует.");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getCommonFilms(Integer id, Integer otherId) {
        return filmStorage.getCommonFilms(id, otherId);
    }

    public List<Film> getFilmsByYear(int id) {
        if (directorStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Режиссёра с таким id - не существует");
        }
        return filmStorage.getFilmsByYear(id);
    }

    public List<Film> getFilmsByLikes(int id) {
        if (directorStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Режиссёра с таким id - не существует");
        }
        return filmStorage.getFilmsByLikes(id);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)) {
            throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                    + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
        }
    }
}