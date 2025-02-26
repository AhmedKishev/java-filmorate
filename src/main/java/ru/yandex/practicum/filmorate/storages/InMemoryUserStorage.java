package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();


    @Override
    public void addUser(User user) {
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
    }

    @Override
    public User updateUser(User user) {
        if (users.get(user.getId()) == null) {
            log.info("Пользователь не найден в update");
            throw new ObjectNotFound("Данного пользователя не существует");
        }
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

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
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
