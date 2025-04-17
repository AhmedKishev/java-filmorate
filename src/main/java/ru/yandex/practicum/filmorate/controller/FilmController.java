package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmController {
    final FilmService filmService;
    static final String PATH_ID_FILM_TO_USER_ID = "/{id}/like/{user-id}";

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.findAllFilms();
    }


    @PutMapping(PATH_ID_FILM_TO_USER_ID)
    public void addLike(@PathVariable("id") int id, @PathVariable("user-id") int userId) {
        filmService.addLike(id, userId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmService.create(film);
        return film;
    }

    @DeleteMapping(PATH_ID_FILM_TO_USER_ID)
    public void deleteLike(@PathVariable("id") int id, @PathVariable("user-id") int userId) {
        filmService.removeLike(id, userId);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }


    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") int id) {
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int count, @RequestParam(required = false) Integer genreId, @RequestParam(required = false) Integer year) {
        if (genreId != null || year != null) {
            return filmService.findPopularByGenreAndYear(count, genreId, year);
        }
        return filmService.findPopular(count);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        filmService.deleteFilmById(filmId);
    }


}
