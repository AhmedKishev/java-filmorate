package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class FilmDbStorage extends BaseRepository<Film> {

    static String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    static String SELECT_FILMS = "SELECT f.film_id, " +
            "f.name, " +
            "f.description, " +
            "f.releaseDate, " +
            "f.duration, " +
            "mpa.rating_id, mpa.name AS mpa_name " +
            "FROM films AS f " +
            "INNER JOIN mpa_rating AS mpa ON f.rating_id = mpa.rating_id ";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }


    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
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
            List<Genre> r = new ArrayList<>(film.getGenres().stream().toList());
            Collections.reverse(r);
            Set<Genre> genres = new LinkedHashSet<>(r);
            film.setGenres(genres);
        }
        if (film.getDirectors() != null) {
            updateDirectors(film.getDirectors(), film.getId());
        }
        return film;
    }


    public Film update(Film film) {
        int id = film.getId();
        String sql = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbc.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), id);
        if (film.getDirectors() != null) {
            if (!film.getDirectors().isEmpty()) {
                updateDirectors(film.getDirectors(), film.getId());
            } else {
                film.setDirectors(new HashSet<>());
                clearDirectors(film.getId());
            }
        } else {
            film.setDirectors(new HashSet<>());
            clearDirectors(film.getId());
        }
        if (film.getGenres() != null) {
            if (!film.getGenres().isEmpty()) {
                updateGenres(film.getGenres(), film.getId());
                List<Genre> r = new ArrayList<>(film.getGenres().stream().toList());
                Collections.reverse(r);
                Set<Genre> genres = new LinkedHashSet<>(r);
                film.setGenres(genres);
            } else {
                clearGenres(film.getId());
                film.setGenres(new HashSet<>());
            }
        } else {
            film.setGenres(new HashSet<>());
            clearGenres(film.getId());
        }
        return film;
    }

    private void clearGenres(Integer id) {
        String deleteGenresForFilm = "DELETE FROM film_genres WHERE film_id=?";
        jdbc.update(deleteGenresForFilm, id);
    }

    private void clearDirectors(Integer id) {
        String deleteDirectorsForFilm = "DELETE FROM film_directors WHERE film_id=?";
        jdbc.update(deleteDirectorsForFilm, id);
    }


    private void updateGenres(Set<Genre> genres, int idFilm) {
        if (!genres.isEmpty()) {
            clearGenres(idFilm);
            final String setFilmGenres = "INSERT INTO film_genres (film_id, genre_id) " + "VALUES(? , ?)";
            for (Genre genre : genres) {
                jdbc.update(setFilmGenres, idFilm, genre.getId());
            }
        }
    }

    private void updateDirectors(Set<Director> directors, Integer idFilm) {
        if (!directors.isEmpty()) {
            clearDirectors(idFilm);
            final String setFilmDirectors = "INSERT INTO film_directors (film_id,director_id) " +
                    "VALUES(?, ?)";
            for (Director director : directors) {
                jdbc.update(setFilmDirectors, idFilm, director.getId());
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
        String sql = "LEFT JOIN likes ON f.film_id = likes.film_id " + "GROUP BY f.film_id " + "ORDER BY COUNT(likes.film_id) DESC " + "LIMIT ?";
        return jdbc.query(SELECT_FILMS + sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private List<Genre> getGenres(int id) {

        String getGenre = "SELECT g.genre_id," +
                "g.name " +
                " FROM film_genres AS fg" +
                " INNER JOIN genres AS g ON g.genre_id=fg.genre_id " +
                "WHERE fg.film_id=?";
        return jdbc.query(getGenre, (rs, rowNum) -> makeGenre(rs), id);
    }


    private List<Director> getDirectors(int id) {
        String getDirectors = "SELECT d.director_id," +
                "d.name " +
                " FROM film_directors AS fd" +
                " INNER JOIN directors AS d ON d.director_id=fd.director_id " +
                " WHERE fd.film_id=?";
        return jdbc.query(getDirectors, (rs, rowNum) -> makeDirector(rs), id);
    }


    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre(0, "");
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        List<Genre> genres = getGenres(id);
        List<Director> directors = getDirectors(id);
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MPA(rs.getInt("rating_id"), rs.getString("mpa_name")))
                .build();
        Set<Genre> genreSet = new LinkedHashSet<>();
        Set<Director> directorSet = new LinkedHashSet<>();
        if (!genres.isEmpty()) {
            Collections.reverse(genres);
            genreSet.addAll(genres);
        }
        if (!directors.isEmpty()) {
            directorSet.addAll(directors);
        }
        film.setDirectors(directorSet);
        film.setGenres(genreSet);
        return film;
    }


    public List<Film> findPopularByGenreAndYear(Integer count, Integer genreId, Integer year) {
        log.debug("Параметры метода: count={}, genreId={}, year={}", count, genreId, year);

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, ").append("mpa.rating_id, mpa.name AS mpa_name ").append("FROM films AS f ").append("INNER JOIN mpa_rating AS mpa ON f.rating_id = mpa.rating_id ").append("LEFT JOIN likes ON f.film_id = likes.film_id ").append("LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id ");

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

        sqlBuilder.append("GROUP BY f.film_id, mpa.rating_id, mpa.name ").append("ORDER BY COUNT(likes.user_id) DESC ").append("LIMIT ?");

        params.add(count);
        log.debug("Итоговый SQL-запрос: {}", sqlBuilder);

        return jdbc.query(sqlBuilder.toString(), (rs, rowNum) -> makeFilm(rs), params.toArray());
    }

    public void deleteFilm(int id) {
        jdbc.update(DELETE_FILM, id);
        log.info("Удалён фильм с ID: {}", id);
    }


    public List<Film> getAllFilmsByDirectorSortByDate(Long directorId) {
        String getAllFilms = "SELECT f.film_id, " +
                "f.name, " +
                "f.description," +
                " f.releaseDate, " +
                "f.duration, " +
                "f.rating_id, m.name AS mpa_name " +
                "FROM film_directors fd " +
                "INNER JOIN films f ON f.film_id = fd.film_id " +
                "LEFT JOIN mpa_rating m ON f.rating_id = m.rating_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.releaseDate ";
        return jdbc.query(getAllFilms, (rs, rowNum) -> makeFilm(rs), directorId);
    }

    public List<Film> getAllFilmsByDirectorFromLikes(long directorId) {
        String getAllFilms = "SELECT f.film_id,f.name AS film_name,f.description,f.releaseDate," +
                "f.duration,f.rating_id,m.name AS mpa_name\n" +
                "FROM films f\n" +
                "JOIN film_directors fd ON f.film_id = fd.film_id\n" +
                "JOIN directors d ON fd.director_id = d.director_id\n" +
                "LEFT JOIN likes l ON f.film_id = l.film_id\n" +
                "LEFT JOIN mpa_rating m ON f.rating_id = m.rating_id\n" +
                "WHERE d.director_id = ?\n" +
                "GROUP BY f.film_id, f.name, f.description, f.releaseDate, f.duration\n" +
                "ORDER BY COUNT(l.user_id) DESC;\n";
        return jdbc.query(getAllFilms, (rs, rowNum) -> makeFilm(rs), directorId);
    }

    public List<Film> findRecommendationsByUserId(int id) {
        String sqlUsersIds = "SELECT fl_other.user_id " +
                "FROM LIKES fl_target " +
                "JOIN LIKES fl_other ON fl_target.film_id = fl_other.film_id " +
                "WHERE fl_target.user_id = ? " +
                "  AND fl_other.user_id != ? " +
                "GROUP BY fl_other.user_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 5";

        List<Integer> similarUsers = jdbc.queryForList(sqlUsersIds, Integer.class, id, id);

        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        String inPlaceholders = String.join(",",
                Collections.nCopies(similarUsers.size(), "?")
        );
        String filmsSql =
                "SELECT f.*, mpa.rating_id AS mpa_rating_id, mpa.name AS mpa_name " +
                        "FROM films f " +
                        "JOIN LIKES l ON f.film_id = l.film_id " +
                        "JOIN mpa_rating mpa ON f.rating_id = mpa.rating_id " +
                        "WHERE l.user_id IN (" + inPlaceholders + ") " +
                        "  AND f.film_id NOT IN (" +
                        "    SELECT film_id " +
                        "    FROM LIKES " +
                        "    WHERE user_id = ? " +
                        "  ) " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.film_id) DESC " +
                        "LIMIT 10";

        List<Object> params = new ArrayList<>(similarUsers);
        params.add(id);
        return jdbc.query(filmsSql, (rs, rowNum) -> makeFilm(rs), params.toArray());
    }

    public List<Film> getCommon(long userId, long friendId) {
        String getFilmsUser = "SELECT DISTINCT f.* ,m.name AS mpa_name" +
                " FROM likes AS l" +
                " INNER JOIN films AS f ON l.film_id=f.film_id" +
                " LEFT JOIN mpa_rating m ON f.rating_id = m.rating_id" +
                " WHERE l.user_id=?";
        List<Film> userFilms = jdbc.query(getFilmsUser, (rs, rowNum) -> makeFilm(rs), userId);
        List<Film> friendFilms = jdbc.query(getFilmsUser, (rs, rowNum) -> makeFilm(rs), friendId);
        Set<Integer> list2Ids = friendFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
        return userFilms.stream()
                .filter(film -> list2Ids.contains(film.getId()))
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilmsByQueryDirector(String query) {
        String getAllFilmsByDirector = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.releaseDate, " +
                "f.duration," +
                " f.rating_id," +
                "m.name AS mpa_name" +
                " FROM films f" +
                " JOIN film_directors fd ON f.film_id = fd.film_id " +
                " JOIN directors d ON fd.director_id = d.director_id " +
                " LEFT JOIN mpa_rating m ON f.rating_id = m.rating_id" +
                " WHERE d.name ILIKE ?";
        String searchPattern = "%" + query + "%";
        return jdbc.query(getAllFilmsByDirector, (rs, rowNum) -> makeFilm(rs), searchPattern);
    }

    public List<Film> getAllFilmsByQueryTitle(String query) {
        String getAllFilmsByTitle = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.releaseDate, " +
                "f.duration," +
                " f.rating_id," +
                "m.name AS mpa_name" +
                " FROM films f" +
                " LEFT JOIN mpa_rating m ON f.rating_id = m.rating_id" +
                " WHERE f.name ILIKE ?";
        String searchPattern = "%" + query + "%";
        return jdbc.query(getAllFilmsByTitle, (rs, rowNum) -> makeFilm(rs), searchPattern);
    }

    public List<Film> getAllFilmsByQueryDirectorAndTitle(String query) {
        List<Film> filmsByTitle = getAllFilmsByQueryTitle(query);
        List<Film> filmsByDirector = getAllFilmsByQueryDirector(query);
        List<Film> allFilms = new ArrayList<>();
        allFilms.addAll(filmsByDirector);
        allFilms.addAll(filmsByTitle);
        return allFilms;
    }


}
