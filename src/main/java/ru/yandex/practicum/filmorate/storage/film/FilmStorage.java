package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    void remove(Integer id);

    Film update(Film film);

    List<Film> getAll();

    Film getById(int id);
}
