package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void removeReview(@PathVariable Integer reviewId) {
        reviewService.removeReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviewsForFilm(@RequestParam(defaultValue = "0", required = false) Integer filmId, @RequestParam(defaultValue = "10", required = false) Integer count) {
        return reviewService.getReviewsForFilm(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void likeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.likeReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void dislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void revokeLikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.revokeLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void revokeDislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.revokeDislikeReview(reviewId, userId);
    }
}
