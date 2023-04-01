package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> add(Film film);

    void remove(Integer id);

    Optional<Film> update(Film film);

    List<Film> getAll();

    Optional<Film> getById(int id);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count);
}
