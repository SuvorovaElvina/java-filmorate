package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTests {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
    }

    @Test
    public void testFindFilmById() {
        Film film = filmStorage.add(new Film("gg", "desc",
                LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of()));
        Optional<Film> filmOptional = filmStorage.getById(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("id", film.getId())
                );
    }


    @Test
    public void testGetFilms() {
        List<Film> films = filmStorage.getAll();

        assertThat(films.size()).isEqualTo(0);
    }

    @Test
    public void testCreateFilm() {
        Film film = filmStorage.add(new Film("gg", "desc",
                LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of()));

        assertThat(film).hasFieldOrPropertyWithValue("id", film.getId())
                .hasFieldOrPropertyWithValue("name", "gg")
                .hasFieldOrPropertyWithValue("description", "desc")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2026, 7, 21))
                .hasFieldOrPropertyWithValue("duration", 100L);
    }

    @Test
    public void testUpdateFilm() {
        Film film = filmStorage.add(new Film("com", "description",
                LocalDate.of(2036, 7, 21), 120L, new Mpa(2, "PG"), List.of()));
        film.setDescription("desc");
        Optional<Film> filmOptional = filmStorage.update(film);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmOpt ->
                        assertThat(filmOpt).hasFieldOrPropertyWithValue("id", film.getId())
                                .hasFieldOrPropertyWithValue("name", "com")
                                .hasFieldOrPropertyWithValue("description", "desc")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2036, 7, 21))
                                .hasFieldOrPropertyWithValue("duration", 120L)
                );
    }

    @Test
    public void testRemoveFilm() {
        Film film = filmStorage.add(new Film("gg", "desc",
                LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of()));
        filmStorage.remove(film.getId());
        Optional<Film> filmOptional = filmStorage.getById(film.getId());

        assertThat(filmOptional).isEmpty();
    }
}
