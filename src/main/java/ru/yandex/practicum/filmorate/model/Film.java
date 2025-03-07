package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

/**
 * Film.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    @NotNull
    String name;
    Long id;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    @Min(1)
    long duration;
}
