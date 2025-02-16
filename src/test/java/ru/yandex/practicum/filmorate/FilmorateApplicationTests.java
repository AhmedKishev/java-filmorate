package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
class FilmorateApplicationTests {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void filmNameNotNull() {
        Film film = new Film();
        film.setDuration(2);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        String info = null;
        for (ConstraintViolation<Film> violation : violations) {
            info = violation.getMessage();
        }
        assertEquals(info, "не должно равняться null");
    }

    @Test
    public void filmDurationMoreZero() {
        Film film = new Film();
        film.setName("Aw");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        String info = null;
        for (ConstraintViolation<Film> violation : violations) {
            info = violation.getMessage();
        }
        assertEquals(info, "должно быть не меньше 1");
    }

    @Test
    public void userWrongEmail() {
        User user = new User();
        user.setEmail("это-неправильный?эмейл@");
        user.setLogin("fwfwwwqwqf");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        String info = null;
        for (ConstraintViolation<User> violation : violations) {
            info = violation.getMessage();
        }
        assertEquals(info, "должно иметь формат адреса электронной почты");
    }


}
