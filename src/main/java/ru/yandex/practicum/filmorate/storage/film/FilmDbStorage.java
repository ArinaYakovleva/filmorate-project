package ru.yandex.practicum.filmorate.storage.film;

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
import java.util.*;
import java.util.stream.Collectors;

@Component("filmStorageDB")
public class FilmDbStorage implements IFilmStorage {

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

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, r.id AS mta_id, r.name AS mta_name, r.description AS mta_description, count(fl.user_id) AS fl_rate " +
                "FROM films f " +
                "JOIN rating r ON r.id = f.rating_id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::getFilm);
        return formFilmsFromQuery(filmList);
    }

    @Override
    public Film getOne(Long id) {
        String sqlQuery = "SELECT f.*, r.id AS mta_id, r.name AS mta_name, r.description AS mta_description, count(fl.user_id) AS fl_rate " +
                "FROM films AS f " +
                "JOIN rating r ON r.id = f.rating_id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE f.id=? " +
                "GROUP BY f.id";
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
    public void remove(Long id) {
        String sqlQuery = "DELETE FROM films WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        return getAll().stream()
                .filter(film -> filterFilmByGenre(film, genreId))
                .filter(film -> filterFilmByYear(film, year))
                .sorted(Comparator.comparing(Film::getRate, Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getDirectorFilms(Long id, String sortBy) {
        String sqlQuery;
        switch (sortBy) {
            case "year":
                sqlQuery = getSqlQueryForSortByYear();
                break;
            case "likes":
                sqlQuery = getSqlQueryForSortByLike();
                break;
            default:
                throw new ValidationException("Передан некорректный параметр", sortBy);
        }
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::getFilm, id);
        if (filmList.isEmpty()) {
            throw new NotFoundException("Фильмы не найдены");
        }
        return getFilms(filmList);
    }

    @Override
    public List<Film> getFilmsBySearch(String directorQuery, String titleQuery) {
        String sqlQuery = "SELECT f.id              AS id,\n" +
                "       f.name            AS name,\n" +
                "       f.rate            AS rate,\n" +
                "       f.description     AS description,\n" +
                "       f.release_date    AS release_date,\n" +
                "       f.duration        AS duration,\n" +
                "       f.rating_id       AS mta_id,\n" +
                "       r.name            AS mta_name,\n" +
                "       r.description     AS mta_description,\n" +
                "       count(fl.user_id) AS fl_rate\n" +
                "FROM films AS f\n" +
                "         LEFT JOIN film_likes fl ON f.id = fl.film_id\n" +
                "         JOIN rating r ON r.id = f.rating_id\n" +
                "         LEFT JOIN film_director fd ON f.id = fd.film_id\n" +
                "         LEFT JOIN director d ON d.id = fd.director_id\n" +
                "WHERE lower(f.name) LIKE '%' || :titleQuery || '%'\n" +
                "   OR lower(d.name) LIKE '%' || :directorQuery || '%'\n" +
                "GROUP BY f.id, r.id\n" +
                "ORDER BY count(fl.film_id) DESC";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("directorQuery", directorQuery);
        params.addValue("titleQuery", titleQuery);

        return getFilms(namedParameterJdbcTemplate.query(sqlQuery, params, this::getFilm));
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT f.*, r.id AS mta_id, r.name AS mta_name, r.description AS mta_description, count(fl.user_id) AS fl_rate " +
                "FROM films f " +
                "JOIN rating r ON r.id = f.rating_id " +
                "JOIN film_likes fl ON f.id = fl.film_id " +
                "JOIN film_likes fl2 ON f.id=fl2.film_id " +
                "WHERE fl.user_id=? AND fl2.user_id=? " +
                "GROUP BY f.id " +
                "ORDER BY fl_rate DESC;";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::getFilm, userId, friendId);
        return formFilmsFromQuery(filmList);
    }

    private List<Film> getFilms(List<Film> filmList) {
        List<Long> listId = filmList.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
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
            filmList.stream()
                    .filter(film -> film.getId().equals(filmId))
                    .findFirst()
                    .get()
                    .getDirectors()
                    .add(new Director(directorId, name));
        });

        return filmList;
    }

    private Film getFilm(ResultSet rs, int rowNum) throws SQLException {

        Long mtaId = rs.getLong("mta_id");
        String mtaName = rs.getString("mta_name");
        String mtaDescription = rs.getString("mta_description");

        return new Film.FilmBuilder(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withDescription(rs.getString("description"))
                .withReleaseDate(rs.getDate("release_date").toLocalDate())
                .withDuration(rs.getInt("duration"))
                .withRate(rs.getLong("fl_rate"))
                .withMpa(new MPARating(mtaId, mtaName, mtaDescription))
                .build();
    }

    private List<Film> formFilmsFromQuery(List<Film> films) {
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, f -> f));
        List<Long> listId = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
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

    private boolean filterFilmByGenre(Film film, Long genreId) {
        return genreId == null || film.getGenres().stream()
                .anyMatch(g -> g.getId().equals(genreId));
    }

    private boolean filterFilmByYear(Film film, Integer year) {
        return year == null || film.getReleaseDate().getYear() == year;
    }

    private String getSqlQueryForSortByYear() {
        return "select f.*, r.id as mta_id, r.name as mta_name, r.description as mta_description, count(fl.user_id) as fl_rate\n" +
                "from films as f\n" +
                "join rating r on r.id = f.rating_id\n" +
                "left join film_likes fl on f.id = fl.film_id\n" +
                "join film_director fd on f.id = fd.film_id\n" +
                "where fd.director_id = ?\n" +
                "group by f.id\n" +
                "order by f.release_date";
    }

    private String getSqlQueryForSortByLike() {
        return "select f.id           as id,\n" +
                "       f.name         as name,\n" +
                "       f.rate         as rate,\n" +
                "       f.description  as description,\n" +
                "       f.release_date as release_date,\n" +
                "       f.duration     as duration,\n" +
                "       f.rating_id    as mta_id,\n" +
                "       r.name         as mta_name,\n" +
                "       r.description  as mta_description,\n" +
                "       count(fl.user_id) as fl_rate\n" +
                "from films as f\n" +
                "         left join film_likes fl on f.id = fl.film_id\n" +
                "         join rating r on r.id = f.rating_id\n" +
                "         join film_director fd on f.id = fd.film_id\n" +
                "where fd.director_id = ?\n" +
                "group by f.id, f.name, f.rate, f.description, f.release_date, f.duration, f.rating_id, r.name, r.description\n" +
                "order by count(fl.film_id)";
    }
}
