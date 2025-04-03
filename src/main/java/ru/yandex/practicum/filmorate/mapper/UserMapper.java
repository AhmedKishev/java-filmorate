package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.User;

public class UserMapper {
    public static User changeVariable(User user, User request) {
        user.setName(request.getName());
        user.setLogin(request.getLogin());
        user.setBirthday(request.getBirthday());
        user.setEmail(request.getEmail());
        user.setId(request.getId());
        return user;
    }

}
