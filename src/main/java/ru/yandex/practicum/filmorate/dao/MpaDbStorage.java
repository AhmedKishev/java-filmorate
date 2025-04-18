package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@FieldDefaults(level = AccessLevel.PACKAGE,
        makeFinal = true)
public class MpaDbStorage {
    JdbcTemplate jdbcTemplate;


    public List<MPA> findAllMpa() {
        String sql = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }


    public Optional<MPA> findMpaById(int id) {
        String sql = "SELECT * FROM mpa_rating where rating_id = ?";
        Optional<MPA> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id).stream().findFirst();
        if (mpa.isEmpty()) throw new ObjectNotFound("MPA не найден");
        else return mpa;
    }

    private MPA makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("rating_id");
        String name = rs.getString("name");
        return new MPA(id, name);
    }
}