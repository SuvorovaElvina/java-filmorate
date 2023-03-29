package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.throwable.ConflictException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public Film add(Film film) {
        film.setId(id++);
        if (films.containsKey(film.getId())) {
            throw new ConflictException("Этот фильм уже создан.");
        } else {
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public void remove(Integer id) {
        films.remove(id);
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException("Данного фильма нет в списках.");
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
    }
}
