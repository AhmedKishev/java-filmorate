package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return this.users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getEmail() == null) {
            log.info("Ошибка при добавлении пользователя. Электронный адрес не может быть пустой");
            throw new ValidationException("Электронный адрес не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.info("Ошибка при добавлении пользователя. Электронный адрес должен содержать @");
            throw new ValidationException("Электронный адрес должен содержать @");
        }
        if (user.getLogin() == null) {
            log.info("Ошибка при добавлении пользователя. Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.info("Ошибка при добавлении пользователя. Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Ошибка при добавлении пользователя. Неверно введена дата рождения");
            throw new ValidationException("Неверно введена дата рождения");
        }
        user.setId(this.getNextId());
        this.users.put(user.getId(), user);
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.info("Ошибка при добавлении пользователя. Не введен id пользователя");
            throw new ValidationException("Не введен id пользователя");
        }
        if (user.getEmail() == null) {
            log.info("Ошибка при добавлении пользователя. Email не может быть пустым");
            throw new ValidationException("Email не может быть пустым");
        }
        if (user.getLogin() == null) {
            log.info("Ошибка при добавлении пользователя. Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        User updateUser = users.get(user.getId());
        updateUser.setLogin(user.getLogin());
        updateUser.setEmail(user.getEmail());
        updateUser.setName(user.getName());
        updateUser.setBirthday(user.getBirthday());
        return updateUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
