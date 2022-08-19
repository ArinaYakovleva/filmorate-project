package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
        String sqlQuery = "select r.user_id,\n" +
                "       r.film_id,\n" +
                "       r.content,\n" +
                "       r.is_positive,\n" +
                "       r.review_id,\n" +
                "       ifnull(sum(case rr.is_positive when true then 1 when false then -1 end), 0) as useful\n" +
                "from reviews r\n" +
                "         left join rating_reviews rr on r.review_id = rr.review_id\n" +
                "where r.film_id=coalesce(?, r.film_id)\n" +
                "group by r.film_id, r.user_id\n" +
                "order by useful desc\n" +
                "limit ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public Review getOne(Long id) {
        String sqlQuery = "select r.user_id,\n" +
                "       r.film_id,\n" +
                "       r.is_positive,\n" +
                "       r.content,\n" +
                "       r.review_id,\n" +
                "       ifnull(sum(case rr.is_positive when true then 1 when false then -1 end), 0) as useful\n" +
                "from reviews r\n" +
                "         left join rating_reviews rr on r.review_id = rr.review_id\n" +
                "where r.review_id = ?\n" +
                "group by r.film_id, r.user_id\n";

        List<Review> reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), id);
        return reviews.size() == 0 ? null : reviews.get(0);
    }

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
        Review review = new Review(
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"));
        review.setUseful(resultSet.getLong("useful"));
        review.setReviewId(resultSet.getLong("review_id"));
        return review;
    }
}
