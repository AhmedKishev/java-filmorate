package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class DirectorController {
    static final String PATH_TO_DIRECTOR_ID = "/{director-id}";
    FilmService filmService;

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return filmService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        return filmService.getAllDirectors();
    }

    @DeleteMapping(PATH_TO_DIRECTOR_ID)
    public void deleteDirector(@PathVariable("director-id") long id) {
        filmService.deleteDirector(id);
    }

    @GetMapping(PATH_TO_DIRECTOR_ID)
    public Optional<Director> getDirectorById(@PathVariable("director-id") long id) {
        return filmService.getDirectorById(id);
    }

}
