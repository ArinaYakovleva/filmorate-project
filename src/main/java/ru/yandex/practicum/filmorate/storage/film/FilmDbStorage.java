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
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
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
        changeFilmDirectors(data);
        return getOne(data.getId());
    }

    @Override
    public Film edit(Film data) {
        String sqlQuery = "UPDATE films SET name=?, rate=?, rating_id=?, description=?, release_date=?, duration=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, data.getName(), data.getRate(), data.getMpa().getId(), data.getDescription(), data.getReleaseDate(), data.getDuration(), data.getId());
        changeFilmGenres(data);
        changeFilmDirectors(data);
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

    private void changeFilmDirectors(Film film) {
        List<Long> listId = new ArrayList<>();
        StringBuilder valueBuilder = new StringBuilder();
        valueBuilder.append("DELETE FROM film_director WHERE film_id=?; ");
        listId.add(film.getId());
        Set<Director> directorSet = film.getDirectors();
        if (!directorSet.isEmpty()) {
            valueBuilder.append("INSERT INTO film_director(film_id, director_id) VALUES ");
            directorSet.forEach(director -> {
                valueBuilder.append("(?, ?),");
                listId.add(film.getId());
                listId.add(director.getId());
            });
        }
        valueBuilder.deleteCharAt(valueBuilder.length() - 1);
        jdbcTemplate.update(valueBuilder.toString(), listId.toArray());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, r.id AS mta_id, r.name AS mta_name, r.description AS mta_description " +
                "FROM films AS f " +
                "JOIN rating r on r.id = f.rating_id";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::getFilm);
        Map<Long, Film> filmMap = filmList.stream().collect(Collectors.toMap(Film::getId, f -> f));
        List<Long> listId = filmList.stream().map(Film::getId).collect(Collectors.toList());
        SqlParameterSource parameters = new MapSqlParameterSource("ids", listId);
        namedParameterJdbcTemplate.query("SELECT fg.film_id, ge.id, ge.name " +
                "FROM film_genre AS fg " +
                "JOIN genres AS ge ON fg.genre_id = ge.id " +
                "WHERE fg.film_id IN (:ids)", parameters, (rs) -> {
            Long filmId = rs.getLong("film_id");
            Long genreId = rs.getLong("id");
            String name = rs.getString("name");
            filmMap.get(filmId).getGenres().add(new Genre(genreId, name));
        });

        namedParameterJdbcTemplate.query("SELECT fd.film_id, d.id, d.name " +
                "FROM film_director AS fd " +
                "JOIN director AS d ON fd.director_id = d.id " +
                "WHERE fd.film_id IN (:ids)", parameters, (rs) -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getLong("id");
            String name = rs.getString("name");
            filmMap.get(filmId).getDirectors().add(new Director(directorId, name));
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

        String directorQuery = "SELECT d.id, d.name " +
                "FROM film_director AS fd " +
                "INNER JOIN director AS d ON fd.director_id = d.id " +
                "WHERE fd.film_id=?";
        Set<Director> directorSet = new HashSet<>(jdbcTemplate.query(directorQuery, (rs, rowNum) -> {
            Long directorId = rs.getLong("id");
            String name = rs.getString("name");
            return new Director(directorId, name);
        }, id));

        assert film != null;
        film.setGenres(genreSet);
        film.setDirectors(directorSet);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getAll().stream()
                .sorted(Comparator.comparing(Film::getRate, Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private String getSqlQueryForSort(String sortBy) {
        switch (sortBy) {
            case "year":
                return "select f.*, r.id as mta_id, r.name as mta_name, r.description as mta_description\n" +
                        "from films as f\n" +
                        "join rating r on r.id = f.rating_id\n" +
                        "join film_director fd on f.id = fd.film_id\n" +
                        "where fd.director_id = ?\n" +
                        "order by f.release_date";

            case "likes":
                return "select f.id           as id,\n" +
                        "       f.name         as name,\n" +
                        "       f.rate         as rate,\n" +
                        "       f.description  as description,\n" +
                        "       f.release_date as release_date,\n" +
                        "       f.duration     as duration,\n" +
                        "       f.rating_id    as mta_id,\n" +
                        "       r.name         as mta_name,\n" +
                        "       r.description  as mta_description\n" +
                        "from films as f\n" +
                        "         left join film_likes fl on f.id = fl.film_id\n" +
                        "         join rating r on r.id = f.rating_id\n" +
                        "         join film_director fd on f.id = fd.film_id\n" +
                        "where fd.director_id = ?\n" +
                        "group by f.id, f.name, f.rate, f.description, f.release_date, f.duration, f.rating_id, r.name, r.description\n" +
                        "order by count(fl.film_id)";

            default:
                String message = "Некорректный параметр сортировки";
                log.warn(message);
                throw new ValidationException(message, sortBy);
        }
    }

    @Override
    public List<Film> getDirectorFilms(Long id, String sortBy) {
        String sqlQuery = getSqlQueryForSort(sortBy);
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::getFilm, id);
        if (filmList.isEmpty()) {
            throw new NotFoundException(String.format("Для режиссера с id = %s не найдено фильмов", id));
        }
        List<Long> listId = filmList.stream().map(Film::getId).collect(Collectors.toList());
        SqlParameterSource parameters = new MapSqlParameterSource("ids", listId);
        namedParameterJdbcTemplate.query("SELECT fg.film_id, ge.id, ge.name " +
                "FROM film_genre AS fg " +
                "JOIN genres AS ge ON fg.genre_id = ge.id " +
                "WHERE fg.film_id IN (:ids)", parameters, (rs) -> {
            Long filmId = rs.getLong("film_id");
            Long genreId = rs.getLong("id");
            String name = rs.getString("name");
            filmList.stream().
                    filter(film -> film.getId().equals(filmId))
                    .findFirst()
                    .get()
                    .getGenres().add(new Genre(genreId, name));
        });

        namedParameterJdbcTemplate.query("SELECT fd.film_id, d.id, d.name " +
                "FROM film_director AS fd " +
                "JOIN director AS d ON fd.director_id = d.id " +
                "WHERE fd.film_id IN (:ids)", parameters, (rs) -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getLong("id");
            String name = rs.getString("name");
            filmList.stream().filter(film -> film.getId().equals(filmId)).findFirst().get().getDirectors().add(new Director(directorId, name));
        });

        return filmList;
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
