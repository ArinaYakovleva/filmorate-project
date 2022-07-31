package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component("filmStorageDB")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film data) {
        String sqlQuery = "INSERT INTO films(name, rate, rating_id, description, release_date, duration) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.getName());
            ps.setLong(2, data.getRate());
            ps.setLong(3, data.getMpa().getId());
            ps.setString(4, data.getDescription());
            ps.setDate(5, java.sql.Date.valueOf(data.getReleaseDate()));
            ps.setInt(6, data.getDuration());
            return ps;
        }, keyHolder);
        data.setId((Long) keyHolder.getKey());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", data.getId());
        List<Genre> genreList = data.getGenres();
        new HashSet<>(genreList).forEach(item -> jdbcTemplate.update("INSERT INTO film_genre(film_id,genre_id) VALUES (?, ?)", data.getId(), item.getId()));
        return getOne(data.getId());
    }

    @Override
    public Film edit(Film data) {
        String sqlQuery = "UPDATE films SET name=?, rate=?, rating_id=?, description=?, release_date=?, duration=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, data.getName(), data.getRate(), data.getMpa().getId(), data.getDescription(), data.getReleaseDate(), data.getDuration(), data.getId());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", data.getId());
        List<Genre> genreList = data.getGenres();
        new HashSet<>(genreList).forEach(item -> jdbcTemplate.update("INSERT INTO film_genre(film_id,genre_id) VALUES (?, ?)", data.getId(), item.getId()));
        return getOne(data.getId());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::getFilm);
    }

    @Override
    public Film getOne(Long id) {
        String sqlQuery = "SELECT * FROM films WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::getFilm, id);
    }

    @Override
    public Long generateId() {
        return null;
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO film_likes(user_id, film_id) VALUES (?, ?)";
        int result = jdbcTemplate.update(sqlQuery, userId, id);
        if (result != 1)
            throw new RuntimeException("Добавлено количество строк отличное от 1. Количество строк с изменениями - " + result);
        jdbcTemplate.update("UPDATE films SET rate=rate+1 WHERE id=?", id);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE user_id=? AND film_id=?";
        jdbcTemplate.update(sqlQuery, userId, id);
        jdbcTemplate.update("UPDATE films SET rate=rate-1 WHERE id=?", id);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getAll().stream()
                .sorted(Comparator.comparing(Film::getRate, Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilm(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        Long rate = rs.getLong("rate");
        Long ratingId = rs.getLong("rating_id");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        return new Film(id, name, description, releaseDate, duration, rate, getMPA(ratingId), getGenres(id));
    }

    private Rating getMPA(Long id) {
        String sqlQuery = "SELECT * FROM rating WHERE id=?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> {
            String name = rs.getString("name");
            String description = rs.getString("description");
            return new Rating(id, name, description);
        }, id);
    }

    private List<Genre> getGenres(Long filmId) {
        String sqlQuery = "SELECT ge.id, ge.name FROM film_genre AS fg INNER JOIN genres AS ge ON fg.genre_id = ge.id WHERE fg.film_id =?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }, filmId);
    }
}
