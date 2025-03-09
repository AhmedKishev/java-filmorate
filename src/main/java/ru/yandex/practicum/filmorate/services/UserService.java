package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> addFriend(int id, int friendId) {
        Optional<User> person = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst();
        Optional<User> friend = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == friendId)
                .findFirst();
        if (person.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + id + " не существует");
        }
        if (friend.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + friendId + " не существует");
        }
        person.get().setFriend(friend.get().getId());
        friend.get().setFriend(person.get().getId());
        return List.of(person.get(), friend.get());
    }

    public void deleteFriend(int id, int friendId) {
        Optional<User> person = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst();
        Optional<User> friend = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == friendId)
                .findFirst();
        if (person.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + id + " не существует");
        }
        if (friend.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + friendId + " не существует");
        }
        person.get().deleteFriend(friend.get().getId());
        friend.get().deleteFriend(person.get().getId());
    }

    public List<User> getFriends(int id) {
        Optional<User> person = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst();
        if (person.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + id + " не существует");
        }
        Set<Long> friendsId = person.get().getFriend();
        List<User> friends = userStorage.getAllUsers().stream()
                .filter(user -> friendsId.contains(user.getId()))
                .collect(Collectors.toList());
        return friends;
    }

    public List<User> getListFriendsWithOtherUser(int id, int otherId) {
        Optional<User> person = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst();
        Optional<User> other = userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == otherId)
                .findFirst();
        if (person.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + id + " не существует");
        }
        if (other.isEmpty()) {
            throw new ObjectNotFound("Пользователя с id " + otherId + " не существует");
        }

        Set<Long> friendsForFirstUser = person.get().getFriend();
        Set<Long> friendsForSecondUser = other.get().getFriend();

        Set<Long> intersection = friendsForSecondUser.stream()
                .filter(friendsForFirstUser::contains)
                .collect(Collectors.toSet());

        return userStorage.getAllUsers().stream()
                .filter(user -> intersection.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
