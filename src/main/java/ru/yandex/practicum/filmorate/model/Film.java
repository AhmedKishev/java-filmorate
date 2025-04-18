package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Set<Genre> genres;
    MPA mpa;
    int likes;
    @NotNull
    String name;
    Set<Director> directors;
    Integer id;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    @Min(1)
    long duration;
}
