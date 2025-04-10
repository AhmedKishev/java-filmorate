package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Repository
public class LikesDbStorage extends BaseRepository<Integer> {
    static final String FIND_FILM_LIKE_BY_ID_USER = "SELECT film_id FROM likes WHERE user_id = ?";
    static final String ADD_LIKE_FOR_FILM = "INSERT INTO likes (film_id,user_id) " +
            "VALUES (?,?)";
    static final String DELETE_LIKE_FOR_FILM_BY_ID_USER = "DELETE FROM likes WHERE film_id=? AND user_id=?";
    static final String GET_ALL_RECORDS = "SELECT * FROM likes";

    public LikesDbStorage(JdbcTemplate jdbc, RowMapper<Integer> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Integer> findLikeById(long userId) {
        return findOne(FIND_FILM_LIKE_BY_ID_USER,
                userId);
    }

    public void addLike(long idFilm, long idUser) {
        insertForTableLike(ADD_LIKE_FOR_FILM,
                idFilm,
                idUser);
    }

    public void deleteLike(long idFilm, long idUser) {
        jdbc.update(DELETE_LIKE_FOR_FILM_BY_ID_USER,
                idFilm,
                idUser);
    }

    public List<Integer> getAllRecords() {
        return findMany(GET_ALL_RECORDS);
    }

    private void insertForTableLike(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

    }

}
