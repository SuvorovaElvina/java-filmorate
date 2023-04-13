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

    @Override
    public Review addReview(Review review) {
        if (review.getFilmId() < 1 || review.getUserId() < 1) {
            throw new NotFoundException("Передан некорректный id фильма или пользователя");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Некорректная характеристика отзыва");
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
        deleteLike(reviewId, userId);
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId);
        log.info("Пользователь id={} отозвал лайк с отзыва id={}.", reviewId, userId);
    }

    @Override
    public void revokeDislikeReview(Integer reviewId, Integer userId) {
        deleteLike(reviewId, userId);
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId);
        log.info("Пользователь id={} отозвал дизлайк с отзыва id={}.", reviewId, userId);
    }

    private void deleteLike(Integer reviewId, Integer userId) {
        String sqlQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            log.warn("Вызван некорректный отзыв или пользователь");
            throw new NotFoundException("Некорректный отзыв или пользователь");
        }
    }

}
