package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
@Slf4j
public class FilmController {
    static final String PATH_ID_FILM_TO_USER_ID = "/{id}/like/{user-id}";
    FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        List<Film> f = filmService.findAllFilms();
        for (Film film : f)
            log.info(f.toString());
        return f;
    }

    @GetMapping("/director/{director-id}")
    public List<Film> getAllFilmsByDirectorWithSort(@PathVariable("director-id") Long directorId, @RequestParam String sortBy) {
        return filmService.getAllFilmsByDirector(directorId, sortBy);
    }

    @PutMapping(PATH_ID_FILM_TO_USER_ID)
    public void addLike(@PathVariable("id") int id, @PathVariable("user-id") int userId) {
        filmService.addLike(id, userId);
    }


    @GetMapping("/common")
    public List<Film> getAllFilmsCommonWithFriend(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getAllFilmsCommon(userId, friendId);
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        return film;
    }

    @GetMapping("/search")
    public List<Film> getAllFilmsByQuery(@RequestParam String query, @RequestParam String by) {
        return filmService.getAllFilmsByQuery(query, by);
    }

    @DeleteMapping(PATH_ID_FILM_TO_USER_ID)
    public void deleteLike(@PathVariable("id") int id, @PathVariable("user-id") int userId) {
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
