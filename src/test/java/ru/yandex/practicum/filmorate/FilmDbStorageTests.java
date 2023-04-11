package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getById(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2)
                );
    }


    @Test
    public void testGetFilms() {
        List<Film> films = filmStorage.getAll();

        assertThat(films.size()).isEqualTo(2);
    }

    @Test
    public void testCreateFilm() {
        Film film = filmStorage.add(new Film("gg", "desc",
                LocalDate.of(2026, 7, 21), 100L,
                new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));

        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "gg")
                .hasFieldOrPropertyWithValue("description", "desc")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2026, 7, 21))
                .hasFieldOrPropertyWithValue("duration", 100L);
    }

    @Test
    public void testUpdateFilm() {
        Optional<Film> filmOptional = filmStorage.update(new Film(1, "com", "description",
                LocalDate.of(2036, 7, 21), 120L,
                new Mpa(2, "PG"), List.of(new Genre(1, "Комедия"))));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "com")
                                .hasFieldOrPropertyWithValue("description", "description")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2036, 7, 21))
                                .hasFieldOrPropertyWithValue("duration", 120L)
                );
    }

    @Test
    public void testGetPopularFilms() {
        List<Film> filmOptional = filmStorage.getPopularFilms(1);
        List<Film> filmOpt = filmStorage.getPopularFilms(10);

        assertThat(filmOptional.size()).isEqualTo(1);
        assertThat(filmOpt.size()).isEqualTo(2);
    }

    @Test
    public void testAddLike() {
        filmStorage.addLike(2, 1);
        List<Film> filmOptional = filmStorage.getPopularFilms(1);

        assertThat(filmOptional.size()).isEqualTo(1);
        assertThat(filmOptional.get(0).getId()).isEqualTo(2);
    }

    @Test
    public void testRemoveLike() {
        filmStorage.removeLike(2, 1);
        List<Film> filmOptional = filmStorage.getPopularFilms(1);

        assertThat(filmOptional.size()).isEqualTo(1);
        assertThat(filmOptional.get(0).getId()).isEqualTo(2);
    }

    @Test
    public void testRemoveFilm() {
        filmStorage.remove(1);
        Optional<Film> filmOptional = filmStorage.getById(1);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void testGetCommonFilms() {
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 1);
        List<Film> filmOptional = filmStorage.getCommonFilms(1, 1);

        assertThat(filmOptional.size()).isEqualTo(1);
        assertThat(filmOptional.get(0).getId()).isEqualTo(1);
    }
}

