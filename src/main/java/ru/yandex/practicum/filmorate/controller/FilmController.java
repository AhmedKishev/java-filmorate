package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return this.films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
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
        film.setId(this.getNextId());
        this.films.put(film.getId(), film);
        return film;
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
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
        Film updateFilm = films.get(film.getId());
        updateFilm.setDuration(film.getDuration());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setName(film.getName());
        updateFilm.setReleaseDate(film.getReleaseDate());
        return updateFilm;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
