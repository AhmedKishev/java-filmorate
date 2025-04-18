package ru.yandex.practicum.filmorate.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class ReviewService {
    ReviewDbStorage reviewStorage;
    FilmDbStorage filmDbStorage;
    UserDbStorage userDbStorage;
    FeedService feedService;

    public Review getReviewById(int id) {
        return reviewStorage.findById(id).orElseThrow(() -> new ObjectNotFound("Отзыв с id " + id + " не найден."));
    }

    public Review updateReview(Review review) {
        validateReview(review.getFilmId(), review.getUserId());
        Review updated = reviewStorage.updateReview(review);
        feedService.addEvent(updated.getUserId(), FeedEvent.EventType.REVIEW, FeedEvent.Operation.UPDATE, updated.getReviewId());
        return updated;
    }

    public Review createReview(Review review) {
        validateReview(review.getFilmId(), review.getUserId());
        Review created = reviewStorage.createReview(review);
        feedService.addEvent(review.getUserId(), FeedEvent.EventType.REVIEW, FeedEvent.Operation.ADD, created.getReviewId());
        return created;
    }

    public void deleteReview(int id) {
        Review review = getReviewById(id); // получаем, чтобы узнать userId
        reviewStorage.deleteReview(id);
        feedService.addEvent(review.getUserId(), FeedEvent.EventType.REVIEW, FeedEvent.Operation.REMOVE, id);
    }

    public List<Review> getReviewByFilmId(int filmId, int count) {
        return reviewStorage.findReviewsByFilm(filmId, count);
    }

    public void likeReview(int reviewId, int userId, boolean isPositive) {
        validateLike(reviewId, userId);
        reviewStorage.addLike(reviewId, userId, isPositive);
    }

    public void removeLike(int reviewId, int userId, boolean isPositive) {
        validateLike(reviewId, userId);
        reviewStorage.removeLike(reviewId, userId, isPositive);
    }

    private void validateLike(int reviewId, int userId) {
        if (reviewStorage.findById(reviewId).isEmpty()) {
            throw new ObjectNotFound("Отзыв с id " + reviewId + " не найден.");
        }
        if (userDbStorage.findById(userId).isEmpty()) {
            throw new ObjectNotFound("Пользователь с id " + userId + " не найден.");
        }
    }

    private void validateReview(int filmId, int userId) {
        if (filmDbStorage.findFilmById(filmId).isEmpty()) {
            throw new ObjectNotFound("Фильм с id " + filmId + " не найден.");
        }
        if (userDbStorage.findById(userId).isEmpty()) {
            throw new ObjectNotFound("Пользователь с id " + userId + " не найден.");
        }
    }
}
