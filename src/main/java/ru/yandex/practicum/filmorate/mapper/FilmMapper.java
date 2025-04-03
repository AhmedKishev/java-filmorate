package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Film;

public class FilmMapper {

    public static Film changeVariable(Film film, Film request) {
        film.setDuration(request.getDuration());
        film.setName(request.getName());
        film.setLikes(request.getLikes());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        return film;
    }

}
