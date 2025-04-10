package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = Review.builder().build();
        review.setReviewId(rs.getLong("id"));
        review.setContent(rs.getString("content"));
        review.setFilmId(rs.getInt("film_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setPositive(rs.getBoolean("is_positive"));
        review.setUseful(rs.getInt("useful"));

        return review;
    }
}
