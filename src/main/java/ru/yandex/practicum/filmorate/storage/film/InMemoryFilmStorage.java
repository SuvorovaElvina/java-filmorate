package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public void add(Film film) {
        film.setId(id++);
        if (films.containsKey(film.getId())) {
            throw new RuntimeException("Этот фильм уже создан.");
        } else {
            films.put(film.getId(), film);
        }
    }

    @Override
    public void remove(Integer id) {
        films.remove(id);
    }

    @Override
    public void update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new RuntimeException("Данного фильма нет в списках.");
        }
    }

    @Override
    public Map<Integer, Film> getAll() {
        return this.films;
    }
}
