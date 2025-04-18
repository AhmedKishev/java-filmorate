package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class DirectorsDbStorage extends BaseRepository<Director> {

    static String INSERT_DIRECTOR = "INSERT INTO directors(name) " +
            "VALUES(?)";
    static String GET_ALL_DIRECTORS = "SELECT * FROM directors";
    static String FIND_BY_ID_DIRECTOR = "SELECT* FROM directors WHERE director_id=?";
    static String UPDATE_QUERY = "UPDATE directors SET name=? WHERE director_id=?";
    static String DELETE_QUERY = "DELETE directors WHERE director_id=?";

    public DirectorsDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public Director create(Director director) {
        long id = Math.toIntExact(insert(INSERT_DIRECTOR,
                director.getName()));
        director.setId(id);
        return director;
    }


    public List<Director> getAllDirectors() {
        return findMany(GET_ALL_DIRECTORS);
    }

    public Director update(Director request) {
        Director updateDirector = this.findOne(FIND_BY_ID_DIRECTOR, request.getId())
                .map(user -> DirectorMapper.changeVariable(user, request))
                .orElseThrow(() -> new ObjectNotFound("Режиссер не найден"));
        update(UPDATE_QUERY,
                updateDirector.getName(),
                updateDirector.getId());
        return updateDirector;
    }

    public void deleteDirector(long id) {
        update(DELETE_QUERY, id);
    }

    public Optional<Director> getDirectorById(long id) {
        Optional<Director> result = findOne(FIND_BY_ID_DIRECTOR, id);
        if (result.isEmpty()) {
            throw new ObjectNotFound("Режиссер не найден");
        }
        return result;
    }
}
