package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review addReview(Review review);

    Optional<Review> updateReview(Review review);

    void removeReview(Integer reviewId);

    Optional<Review> getReviewById(Integer reviewId);

    List<Review> getReviewsForFilm(Integer filmId, Integer count);

    void likeReview(Integer reviewId, Integer userId);

    void dislikeReview(Integer reviewId, Integer userId);

    void revokeLikeReview(Integer reviewId, Integer userId);

    void revokeDislikeReview(Integer reviewId, Integer userId);
}
