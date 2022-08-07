package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmStorageDB")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
        changeFilmGenres(data);
        return getOne(data.getId());
    }

    @Override
    public Film edit(Film data) {
        String sqlQuery = "UPDATE films SET name=?, rate=?, rating_id=?, description=?, release_date=?, duration=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, data.getName(), data.getRate(), data.getMpa().getId(), data.getDescription(), data.getReleaseDate(), data.getDuration(), data.getId());
        changeFilmGenres(data);
        return getOne(data.getId());
    }

    private void changeFilmGenres(Film data) {
        List<Long> listId = new ArrayList<>();
        StringBuilder valueBuilder = new StringBuilder();
        valueBuilder.append("DELETE FROM film_genre WHERE film_id=?; ");
        listId.add(data.getId());
        Set<Genre> genreSet = data.getGenres();
        if (!genreSet.isEmpty()) {
            valueBuilder.append("INSERT INTO film_genre(film_id,genre_id) VALUES ");
            genreSet.forEach(item -> {
                valueBuilder.append("(?, ?),");
                listId.add(data.getId());
                listId.add(item.getId());
            });
        }
        valueBuilder.deleteCharAt(valueBuilder.length() - 1);
        jdbcTemplate.update(valueBuilder.toString(), listId.toArray());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, r.id AS mta_id, r.name AS mta_name, r.description AS mta_description " + "FROM films AS f " + "JOIN rating r on r.id = f.rating_id";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::getFilm);
        Map<Long, Film> filmMap = filmList.stream().collect(Collectors.toMap(Film::getId, f -> f));
        List<Long> listId = filmList.stream().map(Film::getId).collect(Collectors.toList());
        SqlParameterSource parameters = new MapSqlParameterSource("ids", listId);
        namedParameterJdbcTemplate.query("SELECT fg.film_id, ge.id, ge.name FROM film_genre AS fg JOIN genres AS ge ON fg.genre_id = ge.id WHERE fg.film_id IN (:ids)", parameters, (rs) -> {
            Long filmId = rs.getLong("film_id");
            Long genreId = rs.getLong("id");
            String name = rs.getString("name");
            filmMap.get(filmId).getGenres().add(new Genre(genreId, name));
        });
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getOne(Long id) {
        String sqlQuery = "SELECT f.*, r.id AS mta_id, r.name AS mta_name, r.description AS mta_description " + "FROM films AS f " + "JOIN rating r on r.id = f.rating_id WHERE f.id=?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::getFilm, id);
        String genreQuery = "SELECT ge.id, ge.name FROM film_genre AS fg INNER JOIN genres AS ge ON fg.genre_id = ge.id WHERE fg.film_id=?";
        Set<Genre> genreSet = new HashSet<>(jdbcTemplate.query(genreQuery, (rs, rowNum) -> {
            Long genreId = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(genreId, name);
        }, id));
        assert film != null;
        film.setGenres(genreSet);
        return film;
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
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Long mtaId = rs.getLong("mta_id");
        String mtaName = rs.getString("mta_name");
        String mtaDescription = rs.getString("mta_description");
        MPARating mpaRating = new MPARating(mtaId, mtaName, mtaDescription);
        return new Film(id, name, description, releaseDate, duration, rate, mpaRating);
    }

}
