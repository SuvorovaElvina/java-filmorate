package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("Количество фильмов - {}", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilms(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) String count,
                                      @RequestParam(value = "genreId", required = false) Integer genreId,
                                      @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getPopularFilmsOnGenreAndYear((Integer.parseInt(count)), genreId, year);

    }

    @GetMapping("/search")
    public List<Film> getFilmsBySearch(@RequestParam(value = "query", required = false) String query,
                                       @RequestParam(value = "by", required = false) String by) {
        return filmService.getFilmBySearch(query, by);

    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId", defaultValue = "0", required = true) String userId,
                                     @RequestParam(value = "friendId", defaultValue = "0", required = true) String friendId) {
        return filmService.getCommonFilms(Integer.parseInt(userId), Integer.parseInt(friendId));
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable("directorId") int directorId, @RequestParam String sortBy) {
        if (sortBy.equals("year")) {
            return filmService.getFilmsByYear(directorId);
        } else if (sortBy.equals("likes")) {
            return filmService.getFilmsByLikes(directorId);
        } else {
            throw new NotFoundException("Укажите сортировку по какому критерию: year, likes");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable("id") int id) {
        filmService.removeFilm(id);
    }

}