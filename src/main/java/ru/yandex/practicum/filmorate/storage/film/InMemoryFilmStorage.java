package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.throwable.ConflictException;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public Optional<Film> add(Film film) {
        film.setId(id++);
        if (films.containsKey(film.getId())) {
            throw new ConflictException("Этот фильм уже создан.");
        } else {
            films.put(film.getId(), film);
        }
        return Optional.ofNullable(film);
    }

    @Override
    public void remove(Integer id) {
        films.remove(id);
    }

    @Override
    public Optional<Film> update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException("Данного фильма нет в списках.");
        }
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Optional<Film> filmOpt = getById(filmId);
        if (filmOpt.isPresent()) {
            filmOpt.get().getLikes().add(userId);
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        Optional<Film> filmOpt = getById(filmId);
        if (filmOpt.isPresent()) {
            if (filmOpt.get().getLikes().contains(userId)) {
                filmOpt.get().getLikes().remove(userId);
            } else {
                throw new IncorrectCountException("Этот пользователь не ставил лайк.");
            }
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = getAll();
        return films.stream()
                .sorted(Comparator.comparingInt(Film::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
