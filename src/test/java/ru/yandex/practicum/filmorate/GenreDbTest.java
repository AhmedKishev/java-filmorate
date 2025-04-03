package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
public class GenreDbTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void testGetGenreById() {
        Optional<Genre> genre = genreDbStorage.findGenreById(3);

        AssertionsForClassTypes.assertThat(genre)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        AssertionsForClassTypes.assertThat(mpa).hasFieldOrPropertyWithValue("name", "Мультфильм")
                );
    }

}
