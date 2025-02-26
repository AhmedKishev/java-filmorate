package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;
import java.util.*;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Set<Long> friend = new HashSet<>();
    boolean putLike;
    Long id;
    @Email
    @NotNull
    String email;
    @NotNull
    String login;
    String name;
    LocalDate birthday;

    public void setFriend(Long user) {
        friend.add(user);
    }

    public void deleteFriend(Long user) {
        friend.remove(user);
    }
}
