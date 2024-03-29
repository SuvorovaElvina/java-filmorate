package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        return directorStorage.add(director);
    }

    public Director updateDirector(Director director) {
        Optional<Director> directorOpt = directorStorage.update(director);
        return directorOpt.orElseThrow(() -> new NotFoundException(String.format("Режиссёра c id %d - нет в списке зарегистрированных.", director.getId())));
    }

    public List<Director> getDirectors() {
        return directorStorage.getAll();
    }

    public Director getDirector(int id) {
        Optional<Director> directorOpt = directorStorage.getById(id);
        if (directorOpt.isPresent()) {
            return directorOpt.get();
        } else {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException(String.format("Режиссёр с id %d - не существует.", id));
            }
        }
    }

    public void deleteDirector(int id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else if (directorStorage.getById(id).isEmpty()) {
            throw new NotFoundException(String.format("Режиссёр с id %d - не существует.", id));
        }
        directorStorage.remove(id);
    }
}
