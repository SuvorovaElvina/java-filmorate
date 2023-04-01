package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceInMemoryTest {
    private static final Validator validator;
    private static final UserStorage userStorage = new InMemoryUserStorage();
    private static final FilmStorage filmStorage = new InMemoryFilmStorage();
    private static final FilmService service = new FilmService(filmStorage, userStorage);

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void createNewFilm() {
        Film film = new Film("name", "description", LocalDate.of(2000, 1, 12), 120L);
        service.createFilm(film);

        assertNotNull(service.getFilm(3), "Не создаёт фильм - null");
        assertEquals(service.getFilm(3).toString(), film.toString(), "Создаёт неправильный фильм");
    }

    @Test
    public void updateFilm() {
        Film film = new Film("name", "description", LocalDate.of(2000, 1, 12), 120L);
        service.createFilm(film);
        Film film1 = new Film("NAME", "description", LocalDate.of(2000, 1, 12), 120L);
        film1.setId(2);
        service.updateFilm(film1);

        assertNotNull(service.getFilm(2), "Не обновляет фильм - null");
        assertEquals(2, service.getFilm(2).getId(), "Обнавляет не тот фильм");
        assertEquals(service.getFilm(2).toString(), film1.toString(), "Не обновляет фильм");
    }

    @Test
    public void validateNameIsBlank() {
        Film film = new Film("", "description", LocalDate.of(2000, 1, 12), 120L);
        Film film1 = new Film(null, "description", LocalDate.of(2000, 1, 12), 120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся пустое имя");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Создаётся null имя");
    }

    @Test
    public void validateDescriptionMax200() throws ValidationException {
        Film film = new Film("name", "Пятеро друзей ( комик-группа «Шарло»)," +
                " приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова," +
                " который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своё",
                LocalDate.of(2000, 1, 12), 120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся пустое имя");
    }

    @Test
    public void validateReleaseDateBefore1895_12_28() throws ValidationException {
        Film film = new Film("name", "description",
                LocalDate.of(1895, 12, 27), 120L);

        ValidationException thrown = assertThrows(ValidationException.class, () -> service.createFilm(film));
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void validateReleaseDateAndDescriptionIsNull() {
        Film film = new Film("name", "description", null, 120L);
        Film film1 = new Film("name", null, LocalDate.of(2000, 1, 12), 120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся null день релиза");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Создаётся null описание");
    }

    @Test
    public void validateDuration() {
        Film film = new Film("name", "description", LocalDate.of(2000, 1, 12), 0L);
        Film film1 = new Film("name", "description", LocalDate.of(2000, 1, 12), -1L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Создаётся 0 продолжительность фильма");
        violations = validator.validate(film1);
        assertEquals(1, violations.size(), "Создаётся минусовая продолжительность фильма");
    }

    @Test
    public void getListOfFilms() {
        Film film = new Film("name", "description", LocalDate.of(2000, 1, 12), 120L);
        service.createFilm(film);

        assertNotNull(service.getFilms(), "Ничего не добавляет.");
        assertEquals(service.getFilms().size(), 1, "Не добавляет нужное.");

        Film film1 = new Film("NAME", "description", LocalDate.of(2000, 1, 12), 120L);
        film1.setId(1);
        service.updateFilm(film1);

        assertEquals(service.getFilms().size(), 1, "Добавляет излишнее.");
    }

    @Test
    public void addLike() {
        service.addLike(1,1);

        assertNotNull(service.getFilm(1).getLikes(), "Не дабавляет лайки - null");
        assertEquals(service.getFilm(1).getLikes().size(), 1, "Не добавляет лайки.");
    }

    @Test
    public void removeLike() {
        service.removeLike(1, 1);

        assertNotNull(service.getFilm(1).getLikes(), "Не удалаяет лайки - null");
        assertEquals(service.getFilm(1).getLikes().size(), 0, "Не удалаяет лайки.");
    }

    @Test
    public void getPopularFilms() {
        User user = new User("Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        userStorage.add(user);
        service.addLike(1, 1);
        service.getPopularFilms(1);

        assertNotNull(service.getFilm(1).getLikes(), "Не удалаяет лайки - null");
        assertEquals(service.getFilm(1).getLikes().size(), 1, "Не удалаяет лайки.");
    }
}
