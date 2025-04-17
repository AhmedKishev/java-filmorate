package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDbStorage extends BaseRepository<Film> {

    private static final String SELECT_FILMS = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
            "mpa.rating_id, mpa.name AS mpa_name " +
            "FROM films AS f " +
            "INNER JOIN mpa_rating AS mpa ON f.rating_id = mpa.rating_id ";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }


    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, (int) film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        if (film.getGenres() != null) {
            updateGenres(film.getGenres(), film.getId());
        }
        return film;
    }


    public Film update(Film film) {
        int id = film.getId();
        String sql = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbc.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), id);
        return film;
    }


    private void updateGenres(Set<Genre> genres, int idFilm) {
        if (!genres.isEmpty()) {
            final String SET_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) " +
                    "VALUES(? , ?)";
            for (Genre genre : genres) {
                jdbc.update(SET_FILM_GENRES, idFilm, genre.getId());
            }
        }
    }

    public List<Film> findAllFilms() {
        String sql = "ORDER BY f.film_id";
        return jdbc.query(SELECT_FILMS + sql, (rs, rowNum) -> makeFilm(rs));
    }


    public Optional<Film> findFilmById(int id) {
        String sql = "WHERE f.film_id = ?";
        return jdbc.query(SELECT_FILMS + sql, (rs, rowNum) -> makeFilm(rs), id).stream().findFirst();
    }


    public List<Film> findPopular(int count) {
        String sql = "LEFT JOIN likes ON f.film_id = likes.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(likes.film_id) DESC " +
                "LIMIT ?";
        return jdbc.query(SELECT_FILMS + sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private List<Genre> getGenres(int id) {
        String getGenre = "SELECT g.genre_id,g.name " +
                " FROM film_genres AS fg" +
                " INNER JOIN genres AS g ON g.genre_id=fg.genre_id " +
                "WHERE fg.film_id=?";
        return jdbc.query(getGenre, (rs, rowNum) -> makeGenre(rs), id);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre(0, "");
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        List<Genre> genres = getGenres(id);
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MPA(rs.getInt("rating_id"), rs.getString("mpa_name")))
                .build();
        Set<Genre> genreSet = new LinkedHashSet<>();
        if (!genres.isEmpty()) {
            Collections.reverse(genres);
            genreSet.addAll(genres);
        }
        film.setGenres(genreSet);
        return film;
    }

    public List<Film> findPopularByGenreAndYear(Integer count, Integer genreId, Integer year) {
        log.debug("Параметры метода: count={}, genreId={}, year={}", count, genreId, year);

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, ")
                .append("mpa.rating_id, mpa.name AS mpa_name ")
                .append("FROM films AS f ")
                .append("INNER JOIN mpa_rating AS mpa ON f.rating_id = mpa.rating_id ")
                .append("LEFT JOIN likes ON f.film_id = likes.film_id ")
                .append("LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id ");

        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (genreId != null) {
            conditions.add("fg.genre_id = ?");
            params.add(genreId);
        }
        if (year != null) {
            conditions.add("EXTRACT(YEAR FROM f.releaseDate) = ?");
            params.add(year);
        }

        if (!conditions.isEmpty()) {
            sqlBuilder.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sqlBuilder.append("GROUP BY f.film_id, mpa.rating_id, mpa.name ")
                .append("ORDER BY COUNT(likes.user_id) DESC ")
                .append("LIMIT ?");

        params.add(count);
        log.debug("Итоговый SQL-запрос: {}", sqlBuilder);

        return jdbc.query(sqlBuilder.toString(), (rs, rowNum) -> makeFilm(rs), params.toArray());
    }


}
