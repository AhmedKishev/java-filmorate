package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    public Review getReviewById(int id) {
        Optional<Review> reviewOptional = reviewStorage.findById(id);
        if (reviewOptional.isEmpty()) {
            throw new ObjectNotFound("Отзыв с id " + id + " не найден.");
        }
        return reviewOptional.get();
    }

    public Review updateReview(Review review) {
        validateReview(review.getFilmId(), review.getUserId());
        return reviewStorage.updateReview(review);
    }

    public Review createReview(Review review) {
        validateReview(review.getFilmId(), review.getUserId());
        return reviewStorage.createReview(review);
    }

    public void deleteReview(int id) {
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviewByFilmId(int filmId, int count) {
        return reviewStorage.findReviewsByFilm(filmId, count);
    }

    public void likeReview(int review_id, int userId, boolean isPositive) {
        validateLike(review_id, userId);
        reviewStorage.addLike(review_id, userId, isPositive);
    }

    public void removeLike(int review_id, int userId, boolean isPositive) {
        validateLike(review_id, userId);
        reviewStorage.removeLike(review_id, userId, isPositive);
    }

    private void validateLike(int review_id, int userId) {
        if (reviewStorage.findById(review_id).isEmpty()) {
            throw new ObjectNotFound("Отзыв с id " + review_id + " не найден.");
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
