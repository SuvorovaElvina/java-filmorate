package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public Review addReview(Review review) {
        reviewStorage.addReview(review);
        feedStorage.createFeed(review.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        Optional<Review> reviewOptional = reviewStorage.updateReview(review);
        feedStorage.createFeed(reviewOptional.get().getUserId(), "REVIEW", "UPDATE", reviewOptional.get().getReviewId());
        return reviewOptional.orElseThrow(() -> new NotFoundException(String.format("Отзыва с id %d - нет в списке зарегистрированных.", review.getReviewId())));
    }

    public void removeReview(Integer reviewId) {
        feedStorage.createFeed(getReviewById(reviewId).getUserId(), "REVIEW", "REMOVE", reviewId);
        reviewStorage.removeReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        Optional<Review> reviewOptional = reviewStorage.getReviewById(reviewId);
        if (reviewOptional.isPresent()) {
            return reviewOptional.get();
        } else {
            if (reviewId < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0");
            } else {
                throw new NotFoundException(String.format("Отзыв с id %d - не существует", reviewId));
            }
        }
    }

    public List<Review> getReviewsForFilm(Integer filmId, Integer count) {
        return reviewStorage.getReviewsForFilm(filmId, count);
    }

    public void likeReview(Integer reviewId, Integer userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.likeReview(reviewId, userId);
    }

    public void dislikeReview(Integer reviewId, Integer userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.dislikeReview(reviewId, userId);
    }

    public void revokeLikeReview(Integer reviewId, Integer userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.revokeLikeReview(reviewId, userId);
    }

    public void revokeDislikeReview(Integer reviewId, Integer userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.revokeDislikeReview(reviewId, userId);
    }

    private void validateReviewAndUser(Integer reviewId, Integer userId) {
        Optional<Review> reviewOpt = reviewStorage.getReviewById(reviewId);
        Optional<User> userOpt = userStorage.getById(userId);
        if (reviewId < 0) {
            throw new IncorrectCountException("id ревью не должно быть меньше 0.");
        } else if (userId < 0) {
            throw new IncorrectCountException("id пользователя не должно быть меньше 0.");
        } else if (reviewOpt.isEmpty()) {
            throw new NotFoundException(String.format("Отзыва с id %d - не существует", reviewId));
        } else if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format("Пользователя с таким id %d - не существует.", userId));
        }
    }
}
