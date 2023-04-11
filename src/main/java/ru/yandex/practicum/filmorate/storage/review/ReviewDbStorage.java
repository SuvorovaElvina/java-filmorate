package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Integer reviewId);

    Review getReviewById(Integer reviewId);

    List<Review> getReviewsForFilm(Integer filmId);

    void likeReview(Integer reviewId, Integer userId);

    void dislikeReview(Integer reviewId, Integer userId);

    void deleteLikeReview(Integer reviewId, Integer userId);

    void deleteDislikeReview(Integer reviewId, Integer userId);

}
