package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    void add(Film film);

    void remove(Integer id);

    void update(Film film);

    Map<Integer, Film> getAll();
}
