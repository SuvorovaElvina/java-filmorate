package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
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

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        Optional<Review> reviewOptional = reviewStorage.updateReview(review);
        return reviewOptional.orElseThrow(() -> new NotFoundException("Такого отзыва нет в списке зарегистрированных."));
    }

    public void removeReview(Integer reviewId) {
        reviewStorage.removeReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        Optional<Review> reviewOptional = reviewStorage.getReviewById(reviewId);
        if (reviewOptional.isPresent()) {
            return reviewOptional.get();
        } else {
            if (reviewId < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException("Отзыв с указанным id - не существует.");
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
        if (reviewOpt.isEmpty()) {
            throw new NotFoundException("Отзыва с таким id - не существует");
        }
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id - не существует.");
        }
    }

}
