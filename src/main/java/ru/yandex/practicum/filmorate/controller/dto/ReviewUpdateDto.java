package ru.yandex.practicum.filmorate.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewUpdateDto {
    @NotNull
    long reviewId;

    @NotNull
    String content;

    @NotNull
    Integer filmId;

    @NotNull
    Integer userId;

    @NotNull
    Boolean isPositive;

    int useful;
}
