package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Long reviewId;

    @NotBlank
    String content;

    @NotNull
    Integer filmId;

    @NotNull
    Integer userId;


    boolean isPositive;


    int useful;
}
