package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director add(Director director);

    void remove(Integer id);

    Optional<Director> update(Director director);

    List<Director> getAll();

    Optional<Director> getById(int id);
}
