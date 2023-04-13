package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorServiceTest {
    private final DirectorService directorService;

    @Test
    void createDirector() {
        Director director = directorService.createDirector(new Director(1, "director"));

        assertThat(director).hasFieldOrPropertyWithValue("id", 4)
                .hasFieldOrPropertyWithValue("name", "director");
    }

    @Test
    void updateDirector() {
        Director director = directorService.updateDirector(new Director(2, "New Director"));

        assertThat(director).hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "New Director");
    }

    @Test
    void updateDirectorUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            directorService.updateDirector(new Director(9999, "New Director"));
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getDirectors() {
        assertThat(directorService.getDirectors().size()).isEqualTo(1);

        directorService.createDirector(new Director(1, "director"));

        assertThat(directorService.getDirectors().size()).isEqualTo(2);
    }

    @Test
    void getDirector() {
        assertThat(directorService.getDirector(2))
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "director");
    }

    @Test
    void getDirectorUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            directorService.getDirector(9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    public void getDirectorByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            directorService.getDirector(-1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void deleteDirector() {
        directorService.deleteDirector(1);

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            directorService.getDirector(1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void deleteDirectorUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            directorService.deleteDirector(999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }
}