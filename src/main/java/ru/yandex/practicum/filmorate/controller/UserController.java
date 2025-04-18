package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class UserController {
    static final String PATH_USER_ID_TO_FRIEND_ID = "/{id}/friends/{friend-id}";
    UserService userService;
    FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        userService.addUser(user);
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping(PATH_USER_ID_TO_FRIEND_ID)
    public List<User> addFriend(@PathVariable("id") int id, @PathVariable("friend-id") int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(PATH_USER_ID_TO_FRIEND_ID)
    public void deleteFriend(@PathVariable("id") int id, @PathVariable("friend-id") int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{other-id}")
    public List<User> listFriendWithOtherUser(@PathVariable("id") int id, @PathVariable("other-id") int otherId) {
        return userService.getListFriendsWithOtherUser(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsUser(@PathVariable("id") int id) {
        return userService.getFriends(id);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Удаление пользователя с ID: {}", id);
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") int id) {
        log.info("Trying to get recommendations of user with id {}", id);
        List<Film> films = filmService.getRecommendationsById(id);
        log.info("Found {} recommendations", films.size());
        return films;
    }

}
