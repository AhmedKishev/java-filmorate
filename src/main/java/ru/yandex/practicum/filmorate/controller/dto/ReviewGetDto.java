package ru.yandex.practicum.filmorate.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewGetDto {

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
