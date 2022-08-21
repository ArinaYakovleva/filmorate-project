package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.feed.logging.LogEventFeed;

@Component
public class FriendsDbStorage implements IFriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @LogEventFeed
    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "MERGE INTO friends(user_id, friend_id, status) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(sqlQuery, userId, friendId, false);
        if (result != 1)
            throw new RuntimeException("Добавлено количество строк отличное от 1. Количество строк с изменениями - " + result);
    }

    @LogEventFeed
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

}
