package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.List;

@RestController
@RequestMapping("genres")
@RequiredArgsConstructor
public class GenreController {
    final FilmService filmService;

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") int id) {
        return filmService.findGenreById(id);
    }

    @GetMapping
    public List<Genre> getGenres() {
        return filmService.findAllGenres();
    }
}
