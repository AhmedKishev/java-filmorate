package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;


    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.findAllFilms();
    }


    @PutMapping("/{id}/like/{user-Id}")
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("user-Id") int userId) {
        filmService.addLike(id, userId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmService.create(film);
        return film;
    }

    @DeleteMapping("/{id}/like/{user-Id}")
    public void deleteLike(@PathVariable("id") int id,
                           @PathVariable("user-Id") int userId) {
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
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopular(count);
    }


}
