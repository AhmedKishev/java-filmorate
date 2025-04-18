package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class FriendshipDbStorage extends BaseRepository<Long> {
    static String ADD_FRIEND = "INSERT INTO friendship (user_id, friend_id) " +
            "VALUES (?, ?)";
    static String GET_ALL_FRIEND_BY_USER = "SELECT * FROM friendship WHERE user_id = ?";
    static String DELETE_FRIEND_BY_ID = "DELETE FROM friendship WHERE user_id=? AND friend_id=?";

    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<Long> mapper) {
        super(jdbc, mapper);
    }

    public void addFriend(long idUser,
                          long idFriend) {
        insertFriend(ADD_FRIEND,
                idUser,
                idFriend);
    }

    public void deleteFriend(long id, long friendId) {
        jdbc.update(DELETE_FRIEND_BY_ID, id, friendId);
    }

    public List<Long> getAllFriends(long idUser) {
        return findMany(GET_ALL_FRIEND_BY_USER, idUser);
    }

    public void insertFriend(String query, Object... params) {
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
