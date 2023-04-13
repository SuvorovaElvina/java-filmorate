package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
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
    private final UserService userService;
    private final DirectorService directorService;

    @Test
    void createFilm() {
        Film film = filmService.createFilm(new Film(2, "name", "DEScription",
                LocalDate.of(2000, 12,2), 120L, new Mpa(4,"R")));

        assertThat(film).hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "DEScription")
                .hasFieldOrPropertyWithValue("duration", 120L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 12, 2));
    }

    @Test
    void updateFilm() {
        Film film = filmService.updateFilm(new Film(5, "name", "description",
                LocalDate.of(2000, 12,2), 100L, new Mpa(1,"G")));

        assertThat(film).hasFieldOrPropertyWithValue("id", 5)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("duration", 100L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 12, 2));
    }

    @Test
    void updateFilmUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            filmService.updateFilm(new Film(9999, "name", "description",
                    LocalDate.of(2000, 12,2), 120L, new Mpa(1,"G")));
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getFilms() {
        assertThat(filmService.getFilms().size()).isEqualTo(3);
    }

    @Test
    void getFilmById() {
        Film film = filmService.getFilm(5);

        assertThat(film).hasFieldOrPropertyWithValue("id", 5)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("duration", 100L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 12, 2));
    }

    @Test
    void getFilmByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            filmService.getFilm(-1);
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
    void addLike() {
        filmService.createFilm(new Film(1, "name", "description",
                LocalDate.of(2000, 12,2), 120L, new Mpa(1,"G")));

        filmService.addLike(1,1);
        assertThat(filmService.getPopularFilms(1).get(0).getId()).isEqualTo(1);
    }

    @Test
    void removeLike() {
        filmService.removeLike(1,1);

        assertThat(filmService.getPopularFilms(1).get(0).getId()).isEqualTo(1);
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
            filmService.removeLike(1,9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getPopularFilmsNotCount() {
        List<Film> films = filmService.getPopularFilms(10);

        assertThat(films.size()).isEqualTo(2);
    }

    @Test
    void getPopularFilmsWithCount() {
        List<Film> films = filmService.getPopularFilms(1);

        assertThat(films.size()).isEqualTo(1);
    }

    @Test
    void getFilmsByYear() {
        Director director = directorService.createDirector(new Director(1, "director"));
        filmService.updateFilm(new Film(1, "name", "description",
                LocalDate.of(2000, 12,2), 120L, new Mpa(1,"G"),
                List.of(), List.of(director)));
        filmService.updateFilm(new Film(5, "name", "description",
                LocalDate.of(1967, 12,2), 120L, new Mpa(4,"R"),
                List.of(), List.of(director)));

        assertThat(filmService.getFilmsByYear(2).size()).isEqualTo(2);
        assertThat(filmService.getFilmsByYear(2).get(0).getId()).isEqualTo(5);
        assertThat(filmService.getFilmsByYear(2).get(1).getId()).isEqualTo(1);
    }

    @Test
    void getFilmsByLikes() {
        Director director = directorService.createDirector(new Director(1, "Nik Kode"));
        filmService.updateFilm(new Film(1, "name", "description",
                LocalDate.of(2000, 12,2), 120L, new Mpa(1,"G"),
                List.of(), List.of(director)));
        filmService.updateFilm(new Film(5, "name", "description",
                LocalDate.of(1967, 12,2), 120L, new Mpa(4,"R"),
                List.of(), List.of(director)));

        assertThat(filmService.getFilmsByLikes(1).size()).isEqualTo(2);
        assertThat(filmService.getFilmsByLikes(1).get(0).getId()).isEqualTo(1);
        assertThat(filmService.getFilmsByLikes(1).get(1).getId()).isEqualTo(5);
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
                    LocalDate.of(1800, 11,2), 120L, new Mpa(1,"G")));
        });

        Assertions.assertNotNull(thrown.getMessage());
    }
}