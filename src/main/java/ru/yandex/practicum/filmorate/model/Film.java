package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    @NotNull
    private String name;
    private Long id;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private long duration;
}
