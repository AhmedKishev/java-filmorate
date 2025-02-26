package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storages.InMemoryFilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    @PutMapping("/{id}/like/{user-Id}")
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("user-Id") int userId) {
        filmService.addLikeForFilm(id, userId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        inMemoryFilmStorage.addFilm(film);
        return film;
    }

    @DeleteMapping("/{id}/like/{user-Id}")
    public void deleteLike(@PathVariable("id") int id,
                           @PathVariable("user-Id") int userId) {
        filmService.deleteLikeFilm(id, userId);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getFilmsForLike(count);
    }


}
