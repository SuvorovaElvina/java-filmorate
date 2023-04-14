package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;
import ru.yandex.practicum.filmorate.throwable.ValidationException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review addReview(Review review) {
        userStorage.getById(review.getUserId()).orElseThrow(() -> new NotFoundException("Такого пользователя нет в списке зарегистрированных."));
        filmStorage.getById(review.getFilmId()).orElseThrow(() -> new NotFoundException("Такого фильма нет в списке зарегистрированных."));
        if (review.getIsPositive() == null) {
            throw new ValidationException("Некорректная характеристика отзыва");
        }
        if (review.getContent().isBlank() || review.getContent().isEmpty()) {
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        String sqlQuery = "INSERT INTO reviews (content, isPositive, user_id, film_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKeyAs(Integer.class));
        log.info("Отзыв добавлен");

        return review;
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        String sqlQuery = "UPDATE reviews SET content = ?, isPositive = ? WHERE review_id = ?";
        int updateCount = jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (updateCount <= 0) {
            return Optional.empty();
        } else {
            log.info("Отзыв изменен");
            return getReviewById(review.getReviewId());
        }
    }

    @Override
    public void removeReview(Integer reviewId) {
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";
        int updateCount = jdbcTemplate.update(sqlQuery, reviewId);
        if (updateCount <= 0) {
            throw new NotFoundException("Отзыва не существует. Удаление невозможно.");
        }
        log.info("Отзыв удалён");
    }

    @Override
    public Optional<Review> getReviewById(Integer reviewId) {
        try {
            String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";
            Review review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, reviewId);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsForFilm(Integer filmId, Integer count) {
        if (filmId == 0) {
            String sqlQuery = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
        } else {
            String sqlQuery = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("isPositive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful"));
    }

    @Override
    public void likeReview(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id, isLike) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId, true);
        } catch (DataAccessException e) {
            log.warn("Некорректный отзыв или пользователь");
            throw new NotFoundException("Некорректный отзыв или пользователь");
        }
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId);
        log.info("Отзыв id={} получил лайк от пользователя id={}.", reviewId, userId);
    }

    @Override
    public void dislikeReview(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id, isLike) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId, false);
        } catch (DataAccessException e) {
            log.warn("Некорректный отзыв или пользователь");
            throw new NotFoundException("Некорректный отзыв или пользователь");
        }
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId);
        log.info("Отзыв id={} получил дизлайк от пользователя id={}.", reviewId, userId);
    }

    @Override
    public void revokeLikeReview(Integer reviewId, Integer userId) {
        String sqLike = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND isLike = true";
        deleteLike(reviewId, userId, sqLike);
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId);
        log.info("Пользователь id={} отозвал лайк с отзыва id={}.", reviewId, userId);
    }

    @Override
    public void revokeDislikeReview(Integer reviewId, Integer userId) {
        String sqlDislike = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND isLike = false";
        deleteLike(reviewId, userId, sqlDislike);
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId);
        log.info("Пользователь id={} отозвал дизлайк с отзыва id={}.", reviewId, userId);
    }

    private void deleteLike(Integer reviewId, Integer userId, String sql) {
        try {
            int i = jdbcTemplate.update(sql, reviewId, userId);
            if (i <= 0) {
                log.warn("Удаление лайка/дизлайка невозможно");
                throw new NotFoundException("Удаление лайка/дизлайка невозможно");
            }
        } catch (DataAccessException e) {
            log.warn("Вызван некорректный отзыв или пользователь");
            throw new NotFoundException("Некорректный отзыв или пользователь");
        }
    }

}
