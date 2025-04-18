package ru.yandex.practicum.filmorate.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserDbStorage userDbStorage;
    FriendshipDbStorage friendshipDbStorage;
    FeedService feedService;

    public List<User> addFriend(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);

        friendshipDbStorage.addFriend(id, friendId);
        feedService.addEvent(id, FeedEvent.EventType.FRIEND, FeedEvent.Operation.ADD, friendId);
        return getFriends(id);
    }

    public void deleteFriend(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);

        friendshipDbStorage.deleteFriend(id, friendId);
        feedService.addEvent(id, FeedEvent.EventType.FRIEND, FeedEvent.Operation.REMOVE, friendId);
    }


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
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Ошибка при добавлении пользователя. Неверно введена дата рождения");
            throw new ValidationException("Неверно введена дата рождения");
        }


        userDbStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.info("Пользователь не найден в update");
            throw new ObjectNotFound("Данного пользователя не существует");
        }
        Optional<User> isUser = userDbStorage.findById(user.getId());
        if (isUser.isEmpty()) {
            log.info("Пользователь не найден в update");
            throw new ObjectNotFound("Не удалось обновить");
        }
        if (user.getEmail() == null) {
            log.info("Ошибка при добавлении пользователя. Email не может быть пустым");
            throw new ValidationException("Email не может быть пустым");
        }
        if (user.getLogin() == null) {
            log.info("Ошибка при добавлении пользователя. Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        return userDbStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    private List<User> getFriends(List<Long> friendsId, List<User> allUsers) {
        return allUsers.stream().filter(user -> friendsId.contains(user.getId())).collect(Collectors.toList());
    }


    public List<User> getFriends(long userId) {
        Optional<User> person = getAllUsers().stream().filter(user -> user.getId() == userId).findFirst();
        if (person.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + userId + " не существует");
        }
        List<Long> friendsId = friendshipDbStorage.getAllFriends(userId);
        List<User> users = userDbStorage.getAllUsers();
        return getFriends(friendsId, users);
    }

    public List<User> getListFriendsWithOtherUser(int id, int otherId) {
        Optional<User> person = getAllUsers().stream().filter(user -> user.getId() == id).findFirst();
        Optional<User> other = getAllUsers().stream().filter(user -> user.getId() == otherId).findFirst();
        if (person.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + id + " не существует");
        }
        if (other.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + otherId + " не существует");
        }

        List<Long> friendsForFirstUser = friendshipDbStorage.getAllFriends(id);
        List<Long> friendsForSecondUser = friendshipDbStorage.getAllFriends(otherId);

        List<Long> intersection = friendsForSecondUser.stream().filter(friendsForFirstUser::contains).toList();

        return getAllUsers().stream().filter(user -> intersection.contains(user.getId())).collect(Collectors.toList());
    }

    public void deleteUserById(int userId) {
        if (userDbStorage.findById(userId).isEmpty()) {
            throw new ObjectNotFound("Пользователь с id=" + userId + " не найден");
        }
        userDbStorage.deleteUser(userId);
    }

    public User getUserById(int id) {
        return userDbStorage.findById(id).orElseThrow(() -> new ObjectNotFound("Пользователь с id=" + id + " не найден"));
    }

}
