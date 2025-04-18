package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
public class FilmsDbTest {
    private final FilmDbStorage filmDbStorage;
    private final RowMapper<Film> filmRowMapper;
    private Film request;

    @BeforeEach
    public void createFilm() {
        request = Film.builder()
                .id(1)
                .mpa(new MPA(3, "PG-13"))
                .description("Somethng")
                .name("Some")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(100)
                .genres(Set.of(new Genre(2, "Драма")))
                .build();
    }

    @Test
    public void testFindFilmById() {
        filmDbStorage.create(request);
        Optional<Film> filmOptional = filmDbStorage.findFilmById(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2)
                );
    }

    @Test
    public void testUpdateFilm() {
        filmDbStorage.create(request);
        request.setName("Interstellar");
        filmDbStorage.update(request);
        Optional<Film> filmOptional = filmDbStorage.findFilmById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "Interstellar"));
    }

}
