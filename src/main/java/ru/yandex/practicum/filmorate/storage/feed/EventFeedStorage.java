package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.EventFeed;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class EventFeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public EventFeedStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EventFeed> getAll(Long userId) {
        String sqlQuery = "SELECT * FROM event_feed WHERE user_id=?";
        return jdbcTemplate.query(sqlQuery, this::getEventFeed, userId);
    }

    private EventFeed getEventFeed(ResultSet rs, int rowNum) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long userId = rs.getLong("user_id");
        Long entityId = rs.getLong("entity_id");
        String eventType = rs.getString("event_type");
        String operation = rs.getString("operation");
        Long timestamp = rs.getTimestamp("timestamp").getTime();
        return new EventFeed(eventId, userId, entityId, EventType.valueOf(eventType), Operation.valueOf(operation), timestamp);
    }

}
