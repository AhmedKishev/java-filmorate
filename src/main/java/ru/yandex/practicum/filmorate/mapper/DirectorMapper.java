package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Director;

public class DirectorMapper {
    public static Director changeVariable(Director director, Director request) {
        director.setName(request.getName());
        director.setId(request.getId());
        return director;
    }
}
