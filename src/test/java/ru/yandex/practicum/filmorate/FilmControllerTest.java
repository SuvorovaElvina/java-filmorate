package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void createNewFilm() {
        Film film = new Film(2, "name", "description", LocalDate.of(2000, 1, 12), 120L);
        FilmController controller = new FilmController();
        Film newFilm = controller.create(film);

        assertNotNull(newFilm, "Не создаёт фильм - null");
        assertEquals(newFilm.toString(), film.toString(), "Создаёт неправильный фильм");
    }

    @Test
    public void updateFilm() {
        Film film = new Film(1, "name", "description", LocalDate.of(2000, 1, 12), 120L);
        FilmController controller = new FilmController();
        controller.create(film);
        Film film1 = new Film(1, "NAME", "description", LocalDate.of(2000, 1, 12), 120L);
        Film newFilm = controller.update(film1);

        assertNotNull(newFilm, "Не обновляет фильм - null");
        assertEquals(1, newFilm.getId(), "Обнавляет не тот фильм");
        assertEquals(newFilm.toString(), film1.toString(), "Не обновляет фильм");
    }

    @Test
    public void validateNameIsBlank() {
        Film film = new Film(1, "", "description", LocalDate.of(2000, 1, 12), 120L);
        Film film1 = new Film(1, null, "description", LocalDate.of(2000, 1, 12), 120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся пустое имя");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Создаётся null имя");
    }

    @Test
    public void validateDescriptionMax200() throws ValidationException {
        Film film = new Film(1, "name", "Пятеро друзей ( комик-группа «Шарло»)," +
                " приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова," +
                " который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «сво",
                LocalDate.of(2000, 1, 12), 120L);
        FilmController controller = new FilmController();

        ValidationException thrown = assertThrows(ValidationException.class, () -> controller.create(film));
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void validateReleaseDateBefore1895_12_28() throws ValidationException {
        Film film = new Film(1, "name", "description",
                LocalDate.of(1895, 12, 27), 120L);
        FilmController controller = new FilmController();

        ValidationException thrown = assertThrows(ValidationException.class, () -> controller.create(film));
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void validateReleaseDateAndDescriptionIsNull() {
        Film film = new Film(1, "name", "description", null, 120L);
        Film film1 = new Film(1, "name", null, LocalDate.of(2000, 1, 12), 120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся null день релиза");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Создаётся null описание");
    }

    @Test
    public void validateDuration() {
        Film film = new Film(1, "name", "description", LocalDate.of(2000, 1, 12), 0L);
        Film film1 = new Film(1, "name", "description", LocalDate.of(2000, 1, 12), -1L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся 0 продолжительность фильма");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Создаётся минусовая продолжительность фильма");
    }
}
