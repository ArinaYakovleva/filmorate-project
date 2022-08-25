package ru.yandex.practicum.filmorate.storage.recommendation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.IFilmStorage;

import java.util.ArrayList;
import java.util.List;

@Component("UserRecommendationStorage")
public class UserRecommendationStorage implements IRecommendationStorage {

    private final JdbcTemplate jdbcTemplate;
    private final IFilmStorage filmStorage;

    public UserRecommendationStorage(JdbcTemplate jdbcTemplate,
                                     IFilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        var listOfFilms = new ArrayList<Film>();

        String queryForUserId =
                "SELECT user_id, " +
                        "count(film_id) AS cnt " +
                        "FROM film_likes " +
                        "WHERE film_id IN (SELECT film_id FROM film_likes WHERE user_id = ?) " +
                        "AND user_id != ? " +
                        "GROUP BY user_id " +
                        "ORDER BY cnt DESC " +
                        "LIMIT 1";
        var rs = jdbcTemplate.queryForRowSet(queryForUserId, userId, userId);
        if (rs.first()) {
            var userIdWithIntersections = rs.getLong("USER_ID");
            String queryForFilms =
                    "SELECT film_id " +
                            "FROM film_likes " +
                            "WHERE user_id = ? AND film_id NOT IN " +
                            "(SELECT film_id FROM film_likes " +
                            "WHERE user_id = ?)";

            var rsFilms = jdbcTemplate.queryForRowSet(queryForFilms, userIdWithIntersections, userId);
            while (rsFilms.next()) {
                listOfFilms.add(filmStorage.getOne(rsFilms.getLong("FILM_ID")));
            }
        }

        return listOfFilms;
    }
}
