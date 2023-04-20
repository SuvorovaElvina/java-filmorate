package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaServiceTest {
    private final MpaService mpaService;

    @Test
    void getAllMpa() {
        List<Mpa> mpas = mpaService.getAllMpa();

        assertThat(mpas.size()).isEqualTo(10);
    }

    @Test
    void getMpaById() {
        Mpa mpa = mpaService.getMpa(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    void getMpaByIdUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            mpaService.getMpa(9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getMpaByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            mpaService.getMpa(-1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }
}