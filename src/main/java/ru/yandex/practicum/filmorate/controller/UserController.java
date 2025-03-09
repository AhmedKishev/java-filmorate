package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storages.InMemoryUserStorage;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage,
                          UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }


    @GetMapping
    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }


    @PutMapping("/{id}/friends/{friend-Id}")
    public List<User> addFriend(@PathVariable("id") int id,
                                @PathVariable("friend-Id") int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friend-Id}")
    public void deleteFriend(@PathVariable("id") int id,
                             @PathVariable("friend-Id") int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{other-Id}")
    public List<User> listFriendWithOtherUser(@PathVariable("id") int id,
                                              @PathVariable("other-Id") int otherId) {
        return userService.getListFriendsWithOtherUser(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsUser(@PathVariable("id") int id) {
        return userService.getFriends(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        inMemoryUserStorage.addUser(user);
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

}
