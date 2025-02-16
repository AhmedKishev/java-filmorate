package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;


@Data
public class User {
    private Long id;
    @Email
    @NotNull
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
}
