package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
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
    @Qualifier("feedDbStorage")
    private final FeedStorage feedStorage;

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        Optional<Film> filmOptional = filmStorage.update(film);
        return filmOptional.orElseThrow(() -> new NotFoundException(String.format("Фильма с id %d - нет в списке зарегистрированных.", film.getId())));
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
                throw new NotFoundException(String.format("Фильм с id %d - не существует.", id));
            }
        }
    }

    public void removeFilm(int id) {
        validateIdFilm(id);
        filmStorage.remove(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        validateIdFilm(filmId);
        validateIdUser(userId);
        filmStorage.addLike(filmId, userId);
        feedStorage.createFeed(userId, "LIKE", "ADD", filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        validateIdFilm(filmId);
        validateIdUser(userId);
        filmStorage.removeLike(filmId, userId);
        feedStorage.createFeed(userId, "LIKE", "REMOVE", filmId);
    }

    public List<Film> getPopularFilmsOnGenreAndYear(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilmsOnGenreAndYear(count, genreId, year);
    }

    public List<Film> getCommonFilms(Integer id, Integer otherId) {
        return filmStorage.getCommonFilms(id, otherId);
    }

    public List<Film> getFilmsByYear(int id) {
        validateIdDirector(id);
        return filmStorage.getFilmsByYear(id);
    }

    public List<Film> getFilmsByLikes(int id) {
        validateIdDirector(id);
        return filmStorage.getFilmsByLikes(id);
    }

    public List<Film> getFilmBySearch(String query, String by) {
        String querys = query.toLowerCase();
        List<Film> searchfilms;
        switch (by) {
            case ("director"):
                searchfilms = filmStorage.searchFilmByDirectorName(querys);
                return searchfilms;
            case ("title"):
                searchfilms = filmStorage.searchFilmByTitle(querys);
                return searchfilms;
            case ("director,title"):
            case ("title,director"):
                searchfilms = filmStorage.searchFilmByDirectorNameAndTitleFilm(querys);
                return searchfilms;
            default:
                return filmStorage.getAll();
        }
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)) {
            throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                    + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
        }
    }

    private void validateIdFilm(Integer id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else if (filmStorage.getById(id).isEmpty()) {
            throw new NotFoundException(String.format("Фильм с id %d - не существует.", id));
        }
    }

    private void validateIdUser(Integer id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else if (userStorage.getById(id).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id %d - не существует.", id));
        }
    }

    private void validateIdDirector(Integer id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else if (directorStorage.getById(id).isEmpty()) {
            throw new NotFoundException(String.format("Режиссёр с id %d - не существует.", id));
        }
    }
}