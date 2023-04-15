package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreServiceTest {

    private final GenreService genreService;

    @Test
    void getGenres() {
        List<Genre> genres = genreService.getGenres();

        assertThat(genres.size()).isEqualTo(12);
    }

    @Test
    void getGenreById() {
        Genre genres = genreService.getGenre(1);

        assertThat(genres).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void getGenreByIdUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            genreService.getGenre(999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getGenreByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            genreService.getGenre(-1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }
}