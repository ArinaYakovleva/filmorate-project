package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.logging.LogEventFeed;
import ru.yandex.practicum.filmorate.storage.feed.logging.LogEventFeedAround;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class ReviewDbStorage implements IReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @LogEventFeed
    @Override
    public Review add(Review data) {
        String sqlQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES(?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.getContent());
            ps.setBoolean(2, data.getIsPositive());
            ps.setLong(3, data.getUserId());
            ps.setLong(4, data.getFilmId());
            return ps;
        }, keyHolder);

        return getOne((Long) keyHolder.getKey());
    }

    @LogEventFeed
    @Override
    public Review edit(Review data) {
        String sqlQuery = "UPDATE reviews SET content=?, is_positive=? WHERE review_id=?";
        jdbcTemplate.update(sqlQuery,
                data.getContent(),
                data.getIsPositive(),
                data.getReviewId()
        );
        return getOne(data.getReviewId());
    }

    @Override
    public List<Review> getAll(Long filmId, Integer count) {
        String sqlQuery = "SELECT r.user_id,\n" +
                "       r.film_id,\n" +
                "       r.content,\n" +
                "       r.is_positive,\n" +
                "       r.review_id,\n" +
                "       ifnull(sum(CASE rr.is_positive WHEN TRUE THEN 1 WHEN FALSE THEN -1 END), 0) AS useful\n" +
                "FROM reviews r\n" +
                "         LEFT JOIN rating_reviews rr ON r.review_id = rr.review_id\n" +
                "WHERE r.film_id=coalesce(?, r.film_id)\n" +
                "GROUP BY r.film_id, r.user_id, r.content\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public Review getOne(Long id) {
        String sqlQuery = "SELECT r.user_id,\n" +
                "       r.film_id,\n" +
                "       r.is_positive,\n" +
                "       r.content,\n" +
                "       r.review_id,\n" +
                "       ifnull(sum(CASE rr.is_positive WHEN TRUE THEN 1 WHEN FALSE THEN -1 END), 0) AS useful\n" +
                "FROM reviews r\n" +
                "         LEFT JOIN rating_reviews rr ON r.review_id = rr.review_id\n" +
                "WHERE r.review_id = ?\n" +
                "GROUP BY r.film_id, r.user_id\n";

        List<Review> reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), id);
        return reviews.size() == 0 ? null : reviews.get(0);
    }

    @LogEventFeedAround
    @Override
    public void delete(Long reviewId) {
        String sqlQuery = "DELETE FROM reviews WHERE review_id=?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public int like(Long reviewId, Long userId, boolean isLike) {
        String likeQuery = "INSERT INTO rating_reviews(user_id, review_id, is_positive) VALUES(?, ?, ?)";
        return jdbcTemplate.update(likeQuery, userId, reviewId, isLike);
    }

    @Override
    public int deleteLike(Long reviewId, Long userId, boolean isLike) {
        String sqlQuery = "DELETE FROM rating_reviews WHERE review_id=? AND user_id=? AND is_positive=?";
        return jdbcTemplate.update(sqlQuery, reviewId, userId, isLike);
    }

    private Review makeReview(ResultSet resultSet) throws SQLException {
        return new Review(
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"),
                resultSet.getLong("review_id"),
                resultSet.getLong("useful"));
    }
}
