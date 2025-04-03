package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbTest {
    private final UserDbStorage userStorage;
    private final RowMapper<User> userRowMapper;
    private User requestUser;

    @BeforeEach
    public void setUp() {
        requestUser = new User();
        requestUser.setName("A");
        requestUser.setBirthday(LocalDate.of(1990, 12, 12));
        requestUser.setLogin("WEG");
        requestUser.setEmail("aww@wewf");
        requestUser.setId(1L);
    }

    @Test
    public void testFindUserById() {
        userStorage.addUser(requestUser);
        Optional<User> userOptional = userStorage.findById(2);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void testUpdateUser() {
        userStorage.addUser(requestUser);
        requestUser.setEmail("AE");
        userStorage.updateUser(requestUser);
        Optional<User> userOptional = userStorage.findById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("email", "AE"));
    }

}