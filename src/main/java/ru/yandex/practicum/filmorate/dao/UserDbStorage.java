package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class UserDbStorage extends BaseRepository<User> {

    static String INSERT_USER = "INSERT INTO users (email, login, name,birthday)" + " VALUES(?, ?, ?, ?)";
    static String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id=?";
    static String UPDATE_QUERY = "UPDATE users SET email=?, login=?,name=?,birthday=? WHERE user_id=?";
    static String FIND_ALL_USERS = "SELECT * FROM users";
    static String DELETE_USER = "DELETE FROM users WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public User addUser(User user) {
        long id = insert(INSERT_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }


    public User updateUser(User request) {

        User updateUser = this.findById(request.getId())
                .map(user -> UserMapper.changeVariable(user, request))
                .orElseThrow(() -> new ObjectNotFound("Пользователь не найден"));
        updateUser.setId(request.getId());
        update(UPDATE_QUERY, updateUser.getEmail(), updateUser.getLogin(), updateUser.getName(), updateUser.getBirthday(), updateUser.getId());
        return updateUser;

    }

    public Optional<User> findById(long idUser) {
        return findOne(FIND_BY_ID_QUERY, idUser);
    }

    public List<User> getAllUsers() {
        return findMany(FIND_ALL_USERS);
    }

    public void deleteUser(int id) {
        jdbc.update(DELETE_USER, id);
        log.info("Удалён пользователь с ID: {}", id);
    }


}
