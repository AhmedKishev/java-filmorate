package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;


@Data
@FieldDefaults
public class User {
    Long id;
    @Email
    @NotNull
    String email;
    @NotNull
    String login;
    String name;
    LocalDate birthday;
}
