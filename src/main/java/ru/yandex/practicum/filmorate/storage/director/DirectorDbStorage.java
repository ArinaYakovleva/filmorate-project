package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param data Режиссер
     * @return Режиссер
     */
    @Override
    public Director add(Director data) {
        String sqlQuery = "INSERT INTO director(name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.getName());
            return ps;
        }, keyHolder);
        data.setId((Long) keyHolder.getKey());
        return data;
    }

    /**
     * @param data Режиссер
     * @return Режиссер
     */
    @Override
    public Director edit(Director data) {
        String sqlQuery = "UPDATE director SET name=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, data.getName(), data.getId());
        return getOne(data.getId());
    }

    /**
     * @return Список всех режиссеров
     */
    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT id, name from director";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Director(id, name);
        });
    }

    /**
     * @param id режиссера
     * @return Режиссера по id
     */
    @Override
    public Director getOne(Long id) {
        String sqlQuery = "SELECT * FROM director WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> {
            String name = rs.getString("name");
            return new Director(id, name);
        }, id);
    }

    @Override
    public void remove(Long id) {
        String sqlQuery = "DELETE FROM director WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
