package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();
}
