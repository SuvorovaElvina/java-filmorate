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
    void updateDirectorUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            directorService.updateDirector(new Director(9999, "New Director"));
        });

        Assertions.assertNotNull(thrown.getMessage());
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
    void deleteDirectorUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            directorService.deleteDirector(999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }
}