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


    @GetMapping("/director/{director-id}")
    public List<Film> getAllFilmsByDirectorWithSort(@PathVariable("director-id") Long directorId,
                                                    @RequestParam String sortBy) {
        return filmService.getAllFilmsByDirector(directorId, sortBy);
    }

    @PutMapping(PATH_ID_FILM_TO_USER_ID)
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("user-id") int userId) {
        filmService.addLike(id, userId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        return film;
    }

    @GetMapping("/search")
    public List<Film> getAllFilmsByQuery(@RequestParam String query,
                                         @RequestParam String by) {
        return filmService.getAllFilmsByQuery(query, by);
    }

    @DeleteMapping(PATH_ID_FILM_TO_USER_ID)
    public void deleteLike(@PathVariable("id") int id,
                           @PathVariable("user-id") int userId) {
        filmService.removeLike(id, userId);
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }


    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") int id) {
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopular(count);
    }


}
