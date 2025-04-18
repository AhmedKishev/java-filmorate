package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class ReviewDbStorage extends BaseRepository<Review> {

    static String SELECT_REVIEWS = "SELECT * FROM film_reviews ";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Review createReview(Review review) {
        String sql = "INSERT INTO film_reviews (film_id, USER_ID, CONTENT, IS_POSITIVE, USEFUL) VALUES (?, ?, ?, ?, ?)";
        long id = insert(sql,
                review.getFilmId(), review.getUserId(), review.getContent(), review.isPositive(), review.getUseful());
        review.setReviewId(id);
        return review;
    }

    public Review updateReview(Review review) {
        long id = review.getReviewId();
        String sql =
                "UPDATE FILM_REVIEWS SET CONTENT = ?, IS_POSITIVE = ?" +
                        "WHERE ID = ?";
        if (jdbc.update(sql,
                review.getContent(),
                review.isPositive(),
                id) < 1) {
            throw new ObjectNotFound("Отзыв с id " + review.getReviewId() + " не найден.");
        }
        return findById(id).get();
    }

    public void deleteReview(int id) {
        String sql = "DELETE FROM film_reviews WHERE ID = ?";
        jdbc.update(sql, id);
    }

    public List<Review> findReviewsByFilm(int filmId, int count) {
        if (filmId != -1) {
            String sql =
                    "SELECT * FROM film_reviews " +
                            "WHERE FILM_ID = ? " +
                            "ORDER BY USEFUL DESC LIMIT ?";
            return jdbc.query(sql, mapper, filmId, count);
        }
        String sql = "SELECT * FROM film_reviews ORDER BY USEFUL DESC LIMIT ?";
        return jdbc.query(sql, mapper, count);


    }

    public Optional<Review> findById(long id) {
        String sql = SELECT_REVIEWS + "WHERE id = ?";
        return findOne(sql, id);
    }

    public boolean likeExistsById(int reviewId, int userId) {
        String sql = "SELECT COUNT(*) AS count FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, reviewId, userId);
        return count > 0;
    }

    public void addLike(int reviewId, int userId, boolean positive) {
        String sql;
        int amount;

        if (likeExistsById(reviewId, userId)) {
            sql = "UPDATE reviews_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?";
            amount = positive ? 2 : -2;
            update(sql, positive, reviewId, userId);
        } else {
            sql = "INSERT INTO reviews_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)";
            amount = positive ? 1 : -1;
            update(sql, reviewId, userId, positive);
        }

        updateUseful(reviewId, amount);
    }

    public void removeLike(int reviewId, int userId, boolean positive) {
        if (likeExistsById(reviewId, userId)) {
            String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_positive = ?";
            jdbc.update(sql, reviewId, userId, positive);
            int amount = positive ? -1 : 1;
            updateUseful(reviewId, amount);
        }

    }

    private void updateUseful(int reviewId, int amount) {
        String sql = "UPDATE film_reviews SET USEFUL = USEFUL + ? WHERE ID = ?";
        jdbc.update(sql, amount, reviewId);
    }
}
