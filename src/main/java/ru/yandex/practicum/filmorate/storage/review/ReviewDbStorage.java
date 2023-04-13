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
        String sqlQuery = "INSERT INTO reviews (content, isPositive, user_id, film_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.isPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setId(keyHolder.getKeyAs(Integer.class));
        log.info("Отзыв добавлен");

        return review;
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        String sqlQuery = "UPDATE reviews SET content = ?, isPositive = ?, user_id = ?, film_id = ? WHERE review_id = ?";
        Integer reviewId = review.getId();
        int updateCount = jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                reviewId);
        if (updateCount <= 0) {
            return Optional.empty();
        } else {
            log.info("Отзыв изменен");
            return Optional.of(review);
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
            Review review = jdbcTemplate.query(sqlQuery, this::mapRowToReview, reviewId);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsForFilm(Integer filmId, Integer count) {
        if (filmId == 0) {                                                        // УТОЧНИТЬ УСЛОВИЕ ПОСЛЕ КОНТРОЛЛЕРА!
            String sqlQuery = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> mapRowToReview(rs), count);
        } else {
            String sqlQuery = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> mapRowToReview(rs), filmId, count);
        }
    }

    private Review mapRowToReview(ResultSet rs) throws SQLException {
        /*return Review.builder()
                .id(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("isPositive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .build();*/
        return new Review(rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("isPositive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"));
    }

    @Override
    public void likeReview(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id, isLike) VALUES (?, ?, true)";
        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            log.warn("Некорректный отзыв или пользователь");
            throw new NotFoundException("Некорректный отзыв или пользователь");
        }
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId, userId);
        log.info("Отзыв id={} получил лайк от пользователя id={}.", reviewId, userId);
    }

    @Override
    public void dislikeReview(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id, isLike) VALUES (?, ?, false)";
        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            log.warn("Некорректный отзыв или пользователь");
            throw new NotFoundException("Некорректный отзыв или пользователь");
        }
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId, userId);
        log.info("Отзыв id={} получил дизлайк от пользователя id={}.", reviewId, userId);
    }

    @Override
    public void revokeLikeReview(Integer reviewId, Integer userId) {
        deleteLike(reviewId, userId);
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId, userId);
        log.info("Пользователь id={} отозвал лайк с отзыва id={}.", reviewId, userId);
    }

    @Override
    public void revokeDislikeReview(Integer reviewId, Integer userId) {
        deleteLike(reviewId, userId);
        String sqlUsefulUpd = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlUsefulUpd, reviewId, userId);
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
