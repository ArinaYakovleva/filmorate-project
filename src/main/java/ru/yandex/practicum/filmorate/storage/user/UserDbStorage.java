package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Component("userStorageDB")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User data) {
        String sqlQuery = "INSERT INTO users(name, login, email, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.getName());
            ps.setString(2, data.getLogin());
            ps.setString(3, data.getEmail());
            ps.setDate(4, java.sql.Date.valueOf(data.getBirthday()));
            return ps;
        }, keyHolder);
        data.setId((Long) keyHolder.getKey());
        return data;
    }

    @Override
    public User edit(User data) {
        String sqlQuery = "UPDATE users SET name=?, login=?, email=?, birthday=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, data.getName(), data.getLogin(), data.getEmail(), data.getBirthday(), data.getId());
        return getOne(data.getId());
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::getUser);
    }

    @Override
    public User getOne(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::getUser, id);
    }

    @Override
    public void remove(Long id) {
        String sqlQuery = "DELETE FROM users WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> getListCommonFriend(Long userId, Long otherId) {
        String sqlQuery = "SELECT * FROM users AS us " +
                "JOIN friends AS fr1 on us.ID = fr1.friend_id " +
                "JOIN friends AS fr2 on us.ID = fr2.friend_id " +
                "WHERE fr1.user_id = ? AND fr2.user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::getUser, userId, otherId);
    }

    @Override
    public List<User> getListFriend(Long userId) {
        jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=?", this::getUser, userId);
        String sqlQuery = "SELECT * FROM users AS us " +
                "JOIN friends AS fr on us.ID = fr.friend_id " +
                "WHERE fr.user_id=?";
        return jdbcTemplate.query(sqlQuery, this::getUser, userId);
    }

    private User getUser(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

}
