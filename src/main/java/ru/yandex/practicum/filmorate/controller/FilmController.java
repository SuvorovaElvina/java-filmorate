package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate AFTER_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("Количество фильмов - {}", filmService.getFilms().size());
        return new ArrayList<>(filmService.getFilms().values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        filmService.createFilm(film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validateFilm(film);
        filmService.updateFilm(film);
        return film;
    }

    @GetMapping("/{id}")
    public Film getFilms(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        if (count == null) {
            return filmService.getPopularFilms(10);
        } else {
            return filmService.getPopularFilms(Integer.parseInt(count));
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.removeLike(id, userId);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(AFTER_RELEASE_DATE)) {
            log.error("Не прошёл валидацию.");
            throw new ValidationException("Фильм должен быть не раньше " + AFTER_RELEASE_DATE.getDayOfMonth()
                    + " " + AFTER_RELEASE_DATE.getMonth() + " " + AFTER_RELEASE_DATE.getYear());
        }
        log.debug(film.toString());
    }
}
