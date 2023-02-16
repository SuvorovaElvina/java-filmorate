package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer , Film> films = new HashMap<>();
    private static final LocalDate AFTER_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private Integer id = 1;

    @GetMapping
    public ArrayList<Film> findAll() {
        log.debug("Количество фильмов - {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        try {
            if (!films.containsKey(film.getId())) {
                if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)){
                    throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                            + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
                } else if (film.getDescription().length() >= 200) {
                    throw new ValidationException("Описание фильма должно быть меньше 200 символов.");
                }
                log.debug(String.valueOf(film));
                film.setId(id++);
                films.put(film.getId(), film);
            } else {
                throw new ValidationException("Этот фильм уже создан.");
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        try {
            if (films.containsKey(film.getId())) {
                if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)){
                    throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                            + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
                } else if (film.getDescription().length() >= 200) {
                    throw new ValidationException("Описание фильма должно быть меньше 200 символов.");
                }
                log.debug(String.valueOf(film));
                films.put(film.getId(), film);
            } else {
                throw new ValidationException("Данного фильма нет в списках.");
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
        return film;
    }
}
