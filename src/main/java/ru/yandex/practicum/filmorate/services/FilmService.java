package ru.yandex.practicum.filmorate.services;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;

import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    final FilmDbStorage filmDbStorage;
    final UserDbStorage userDbStorage;
    final LikesDbStorage likesDbStorage;
    final GenreDbStorage genreDbStorage;
    final MpaDbStorage mpaDbStorage;
    final DirectorsDbStorage directorsDbStorage;

    public Film createFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.info("Ошибка при добавлении фильма. Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Ошибка при добавлении фильма. Описание не может быть больше 200 символов");
            throw new ValidationException("Описание не может быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка при добавлении фильма. Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Ошибка при добавлении фильма. Длительность фильма должна быть положительным числом");
            throw new ValidationException("Длительность фильма должна быть положительным числом");
        }

        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 1) {
            throw new ObjectNotFound("Неверный MPA");
        }
        if (!(film.getGenres() == null)) {
            if (!film.getGenres().stream().filter(genre -> genre.getId() > 6).collect(Collectors.toSet()).isEmpty()) {
                throw new ObjectNotFound("Такого жанра не существует");
            }
        }
        return filmDbStorage.create(film);
    }


    public Director createDirector(Director director) {
        if (director.getName() == null) {
            throw new ValidationException("У режиссера должно быть имя");
        }
        return directorsDbStorage.create(director);
    }


    public List<Director> getAllDirectors() {
        return directorsDbStorage.getAllDirectors();
    }

    public Film updateFilm(Film film) {
        if (filmDbStorage.findFilmById(film.getId()).isEmpty()) {
            throw new ObjectNotFound("Фильм не найден.");
        }
        if (film.getId() == null) {
            log.info("Ошибка при изменении информации о фильме. Не введен id фильма");
            throw new ValidationException("Не введен id фильма");
        }
        if (film.getDescription().length() > 200) {
            log.info("Ошибка при изменении информации о фильме. Описание не может быть больше 200 символов");
            throw new ValidationException("Описание не может быть больше 200 символов");
        }
        if (film.getDuration() < 0) {
            log.info("Ошибка при изменении информации о фильме. Длительность фильма должна быть положительным числом");
            throw new ValidationException("Длительность фильма должна быть положительным числом");
        }
        if (filmDbStorage.findFilmById(film.getId()).isEmpty()) {
            throw new ObjectNotFound("Фильм не найден.");
        }
        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 1) {
            throw new ObjectNotFound("Неверный MPA");
        }
        return filmDbStorage.update(film);
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmDbStorage.findAllFilms();
        return films;
    }

    public Film findFilmById(int id) {
        Film film = filmDbStorage.findFilmById(id).get();
        if (id == 10) {
            log.info(film.toString());
        }
        return film;
    }

    public void addLike(int id, int userId) {
        if (userDbStorage.findById(id).isEmpty() || userDbStorage.findById(userId).isEmpty()) {
            throw new ObjectNotFound("Пользователь не найден.");
        }
        likesDbStorage.addLike(id, userId);
    }

    public void removeLike(int id, int userId) {
        if (userDbStorage.findById(id).isEmpty() || userDbStorage.findById(userId).isEmpty()) {
            throw new ObjectNotFound("Пользователь не найден.");
        }
        likesDbStorage.deleteLike(id, userId);
    }

    public List<Film> findPopular(int count) {
        List<Film> films = filmDbStorage.findPopular(count);
        genreDbStorage.findAllGenresByFilm(films);
        return films;
    }

    public List<MPA> findAllMpa() {
        return mpaDbStorage.findAllMpa();
    }

    public MPA findMpaById(int id) {
        return mpaDbStorage.findMpaById(id).orElseThrow(() -> new ObjectNotFound("Рейтинг MPA не найден."));
    }

    public List<Genre> findAllGenres() {
        return genreDbStorage.findAllGenres();
    }

    public Genre findGenreById(int id) {
        return genreDbStorage.findGenreById(id).orElseThrow(() -> new ObjectNotFound("Жанр не найден."));
    }


    public Director updateDirector(Director director) {
        return directorsDbStorage.update(director);
    }

    public void deleteDirector(long id) {
        directorsDbStorage.deleteDirector(id);
    }

    public Optional<Director> getDirectorById(long id) {
        return directorsDbStorage.getDirectorById(id);
    }

    public List<Film> getAllFilmsByDirector(Long directorId, String sortBy) {
        if (sortBy.equals("year")) {
            return filmDbStorage.getAllFilmsByDirectorSortByDate(directorId);
        } else if (sortBy.equals("likes")) {
            return filmDbStorage.getAllFilmsByDirectorFromLikes(directorId);
        } else throw new ValidationException("Такой сортировки не существует");


    public List<Film> getRecommendationsById(int id) {
        if (userDbStorage.findById(id).isEmpty()) {
            throw new ObjectNotFound("Пользователь не найден.");
        }
        return filmDbStorage.findRecommendationsByUserId(id);

    }
}
