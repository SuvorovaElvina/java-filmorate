package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    void remove(Integer id);

    Optional<Film> update(Film film);

    List<Film> getAll();

    Optional<Film> getById(int id);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopularFilmsOnGenreAndYear(Integer count, Integer genreId, Integer year);

    List<Film> getLikes(int userId, List<Film> films);

    Map<User, HashMap<Film, Double>> getRecommendationData(List<User> users, List<Film> films);

    List<Film> getFilmsByYear(Integer count);

    List<Film> getFilmsByLikes(Integer count);

    List<Film> searchFilmByTitle(String title);

    List<Film> searchFilmByDirectorName(String title);

    List<Film> searchFilmByDirectorNameAndTitleFilm(String dirfilname);
}
