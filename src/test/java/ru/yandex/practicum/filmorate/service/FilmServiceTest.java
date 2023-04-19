package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {
    private final FilmService filmService;

    @Test
    void createFilm() {
        Film film = filmService.createFilm(new Film("name", "description",
                LocalDate.of(2000,7,4), 100L, new Mpa(1, "G"), List.of()));

        assertThat(film).hasFieldOrPropertyWithValue("id", film.getId())
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("duration", 100L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 7, 4));
    }

    @Test
    void updateFilm() {
        Film film1 = filmService.createFilm(new Film("name", "description",
                LocalDate.of(2000,7,4), 100L, new Mpa(1, "G"), List.of()));
        film1.setReleaseDate(LocalDate.of(2000, 12, 2));
        Film film = filmService.updateFilm(film1);

        assertThat(film).hasFieldOrPropertyWithValue("id", film.getId())
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("duration", 100L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 12, 2));
    }

    @Test
    void updateFilmUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.updateFilm(new Film(9999, "name", "description",
                    LocalDate.of(2000, 12, 2), 120L, new Mpa(1, "G")));
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getFilmById() {
        Film film1 = filmService.createFilm(new Film("name", "description",
                LocalDate.of(2000,7,4), 100L, new Mpa(1, "G"), List.of()));
        Film film = filmService.getFilm(film1.getId());

        assertThat(film).hasFieldOrPropertyWithValue("id", film.getId())
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("duration", 100L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000,7,4));
    }

    @Test
    void getFilmByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            filmService.getFilm(-10);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getFilmByIdUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.getFilm(9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void addLikeFilmIsUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.addLike(9999, 1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void addLikeUserIsUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.addLike(1, 9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void removeLikeFilmIsUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.removeLike(9999, 1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void removeLikeUserIsUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.removeLike(1, 9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getPopularFilmsWithCount() {
        List<Film> films = filmService.getPopularFilmsOnGenreAndYear(10, 1, 1999);

        assertThat(films.size()).isEqualTo(0);
    }

    @Test
    void getFilmsByYearWhereIdIsUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.getFilmsByYear(9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getFilmsByLikesWhereIdIsUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.getFilmsByLikes(9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void createFilmFailLogin() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            filmService.createFilm(new Film(1, "name", "description",
                    LocalDate.of(1800, 11, 2), 120L, new Mpa(1, "G")));
        });

        Assertions.assertNotNull(thrown.getMessage());
    }
}