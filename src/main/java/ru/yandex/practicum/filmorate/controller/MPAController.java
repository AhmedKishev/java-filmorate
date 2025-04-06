package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MPAController {
    final FilmService filmService;

    @GetMapping
    public List<MPA> getMpa() {
        return filmService.findAllMpa();
    }

    @GetMapping("/{id}")
    public MPA getMpaById(@PathVariable("id") int id) {
        return filmService.findMpaById(id);
    }
}
