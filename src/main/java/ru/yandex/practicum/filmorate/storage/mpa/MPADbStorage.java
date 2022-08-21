package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

@Component
public class MPADbStorage implements IMPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPARating> getAll() {
        String sqlQuery = "SELECT * FROM rating";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            return new MPARating(id, name, description);
        });
    }

    @Override
    public MPARating getOne(Long id) {
        String sqlQuery = "SELECT * FROM rating WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> {
            String name = rs.getString("name");
            String description = rs.getString("description");
            return new MPARating(id, name, description);
        }, id);
    }

}
