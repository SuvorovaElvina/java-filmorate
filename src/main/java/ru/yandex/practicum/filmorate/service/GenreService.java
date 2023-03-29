package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.List;

public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenre(int id) {
        if (genreStorage.getById(id) != null) {
            return genreStorage.getById(id);
        } else {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException("Фильм с указанный id - не существует.");
            }
        }
    }
}
