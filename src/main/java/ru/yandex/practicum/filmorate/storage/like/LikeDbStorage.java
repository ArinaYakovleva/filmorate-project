package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.feed.logging.LogEventFeed;

@Component
public class LikeDbStorage implements ILikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @LogEventFeed
    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "MERGE INTO film_likes(user_id, film_id) VALUES (?, ?); UPDATE films SET rate=rate+1 WHERE id=?;";
        int result = jdbcTemplate.update(sqlQuery, userId, id, id);
        if (result != 1)
            throw new RuntimeException("Добавлено количество строк отличное от 1. Количество строк с изменениями - " + result);
    }

    @LogEventFeed
    @Override
    public void deleteLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE user_id=? AND film_id=?; UPDATE films SET rate=rate-1 WHERE id=?;";
        jdbcTemplate.update(sqlQuery, userId, id, id);
    }

}
