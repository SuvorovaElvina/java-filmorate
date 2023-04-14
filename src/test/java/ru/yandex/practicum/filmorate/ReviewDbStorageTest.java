package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void addReview_returnReview() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        assertThat(review).hasFieldOrPropertyWithValue("reviewId", review.getReviewId())
                .hasFieldOrPropertyWithValue("content", "шляпа")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("userId", user.getId())
                .hasFieldOrPropertyWithValue("filmId", film.getId())
                .hasFieldOrPropertyWithValue("useful", null);
    }

    @Test
    void addReview_wrongFilm() {
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));

        NotFoundException wrongUser = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.addReview(new Review("шляпа", false, user.getId(), 10))
        );
        Assertions.assertEquals("Такого фильма нет в списке зарегистрированных.", wrongUser.getMessage());
    }

    @Test
    void addReview_wrongUser() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));

        NotFoundException wrongFilm = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.addReview(new Review("шляпа", false, 10, film.getId()))
        );
        Assertions.assertEquals("Такого пользователя нет в списке зарегистрированных.", wrongFilm.getMessage());
    }

    @Test
    void addReview_wrongContent() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));

        ValidationException wrongPositive = assertThrows(
                ValidationException.class,
                () -> reviewStorage.addReview(new Review("", false, user.getId(), film.getId()))
        );
        Assertions.assertEquals("Содержание отзыва не может быть пустым", wrongPositive.getMessage());
    }

    @Test
    void addReview_wrongPositive() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));

        ValidationException wrongPositive = assertThrows(
                ValidationException.class,
                () -> reviewStorage.addReview(new Review("шляпа", null, user.getId(), film.getId()))
        );
        Assertions.assertEquals("Некорректная характеристика отзыва", wrongPositive.getMessage());
    }

    @Test
    void updateReview_returnReview() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        assertThat(reviewStorage.updateReview(new Review(review.getReviewId(), "да лан пойдет", true, user.getId(), film.getId())))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("reviewId", review.getReviewId())
                                .hasFieldOrPropertyWithValue("content", "да лан пойдет")
                                .hasFieldOrPropertyWithValue("isPositive", true)
                                .hasFieldOrPropertyWithValue("userId", user.getId())
                                .hasFieldOrPropertyWithValue("filmId", film.getId()));
    }

    @Test
    void updateReview_wrongReview() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        NoSuchElementException wrongReview = assertThrows(
                NoSuchElementException.class,
                () -> reviewStorage.updateReview(new Review(100, "да лан пойдет", true, user.getId(), film.getId())).get()
        );
        Assertions.assertEquals("No value present", wrongReview.getMessage());
    }

    @Test
    void updateReview_wrongUserOrFilm() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        assertThat(reviewStorage.updateReview(new Review(review.getReviewId(), "да лан пойдет", true, 100, 100)))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("reviewId", review.getReviewId())
                                .hasFieldOrPropertyWithValue("content", "да лан пойдет")
                                .hasFieldOrPropertyWithValue("isPositive", true)
                                .hasFieldOrPropertyWithValue("userId", user.getId())
                                .hasFieldOrPropertyWithValue("filmId", film.getId()));
    }

    @Test
    void removeReview_returnEmptiness() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));
        reviewStorage.removeReview(review.getReviewId());

        assertThat(reviewStorage.getReviewById(review.getReviewId())).isEmpty();
    }

    @Test
    void removeReview_wrongReview() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        NotFoundException wrongReview = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.removeReview(100)
        );
        Assertions.assertEquals("Отзыва не существует. Удаление невозможно.", wrongReview.getMessage());
    }

    @Test
    void getReviewById_returnReview() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        assertThat(reviewStorage.getReviewById(review.getReviewId()))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("reviewId", review.getReviewId())
                                .hasFieldOrPropertyWithValue("content", "шляпа")
                                .hasFieldOrPropertyWithValue("isPositive", false)
                                .hasFieldOrPropertyWithValue("userId", user.getId())
                                .hasFieldOrPropertyWithValue("filmId", film.getId()));
    }

    @Test
    void getReviewById_wrongId() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        reviewStorage.addReview(new Review("шляпа", false, user.getId(), film.getId()));

        NoSuchElementException wrongReview = assertThrows(
                NoSuchElementException.class,
                () -> reviewStorage.getReviewById(-10).get()
        );
        Assertions.assertEquals("No value present", wrongReview.getMessage());
    }

    @Test
    void getReviewsForFilm_returnSize() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        User user2 = userStorage.add(new User("mail@mail.ru", "logg", "Nice", LocalDate.of(1998, 5, 10)));
        reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));
        reviewStorage.addReview(new Review("ы лол", true, user2.getId(), film.getId()));

        Assertions.assertEquals(2, reviewStorage.getReviewsForFilm(film.getId(), 10).size());
        Assertions.assertEquals(2, reviewStorage.getReviewsForFilm(0, 10).size());
        Assertions.assertEquals(1, reviewStorage.getReviewsForFilm(film.getId(), 1).size());
        Assertions.assertEquals(0, reviewStorage.getReviewsForFilm(100, 10).size());
    }

    @Test
    void likeReview_returnUseful() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        User user2 = userStorage.add(new User("mail@mail.ru", "logg", "Nice", LocalDate.of(1998, 5, 10)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));
        reviewStorage.likeReview(review.getReviewId(), user1.getId());
        reviewStorage.likeReview(review.getReviewId(), user2.getId());

        assertThat(reviewStorage.getReviewById(review.getReviewId()))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("useful", 2)
                );
    }

    @Test
    void likeReview_wrongReview() {
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));

        NotFoundException wrongReview = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.likeReview(10, user1.getId())
        );
        Assertions.assertEquals("Некорректный отзыв или пользователь", wrongReview.getMessage());
    }

    @Test
    void dislikeReview_returnUseful() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        User user2 = userStorage.add(new User("mail@mail.ru", "logg", "Nice", LocalDate.of(1998, 5, 10)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));
        reviewStorage.dislikeReview(review.getReviewId(), user1.getId());
        reviewStorage.dislikeReview(review.getReviewId(), user2.getId());

        assertThat(reviewStorage.getReviewById(review.getReviewId()))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("useful", -2)
                );
    }

    @Test
    void dislikeReview_wrongReview() {
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));

        NotFoundException wrongReview = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.dislikeReview(10, user1.getId())
        );
        Assertions.assertEquals("Некорректный отзыв или пользователь", wrongReview.getMessage());
    }

    @Test
    void revokeLikeReview_returnUseful() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        User user2 = userStorage.add(new User("mail@mail.ru", "logg", "Nice", LocalDate.of(1998, 5, 10)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));
        reviewStorage.likeReview(review.getReviewId(), user1.getId());
        reviewStorage.likeReview(review.getReviewId(), user2.getId());
        reviewStorage.revokeLikeReview(review.getReviewId(), user2.getId());

        assertThat(reviewStorage.getReviewById(review.getReviewId()))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("useful", 1)
                );
    }

    @Test
    void revokeLikeReview_revokeWithoutLike() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));

        NotFoundException wrongLike = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.revokeLikeReview(review.getReviewId(), user1.getId())
        );
        Assertions.assertEquals("Удаление лайка/дизлайка невозможно", wrongLike.getMessage());
    }

    @Test
    void revokeDislikeReview_returnUseful() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        User user2 = userStorage.add(new User("mail@mail.ru", "logg", "Nice", LocalDate.of(1998, 5, 10)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));
        reviewStorage.dislikeReview(review.getReviewId(), user1.getId());
        reviewStorage.dislikeReview(review.getReviewId(), user2.getId());
        reviewStorage.revokeDislikeReview(review.getReviewId(), user2.getId());

        assertThat(reviewStorage.getReviewById(review.getReviewId()))
                .isPresent()
                .hasValueSatisfying(review1 ->
                        assertThat(review1).hasFieldOrPropertyWithValue("useful", -1)
                );
    }

    @Test
    void revokeLikeReview_revokeWithoutDislike() {
        Film film = filmStorage.add(new Film("gg", "desc", LocalDate.of(2026, 7, 21), 100L, new Mpa(1, "G"), List.of(new Genre(1, "Комедия"))));
        User user1 = userStorage.add(new User("gmail@mail.ru", "log", "Nike", LocalDate.of(1999, 7, 21)));
        Review review = reviewStorage.addReview(new Review("шляпа", false, user1.getId(), film.getId()));

        NotFoundException wrongLike = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.revokeDislikeReview(review.getReviewId(), user1.getId())
        );
        Assertions.assertEquals("Удаление лайка/дизлайка невозможно", wrongLike.getMessage());
    }

}
