package ru.yandex.practicum.filmorate.storage.feed.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class EventFeedBase {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventFeedBase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void execute(Long userId, EventType eventType, Operation operation, Long entityId) {
        String sqlQuery = "INSERT INTO event_feed(user_id, event_type, operation, entity_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        int result = jdbcTemplate.update(sqlQuery, userId, eventType.name(), operation.name(), entityId, timestamp);
        if (result != 1)
            throw new RuntimeException("Добавлено количество строк отличное от 1. Количество строк с изменениями - " + result);
    }

    public Long getId(String sqlQuery, String item, Long searchItem) {
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> rs.getLong(item), searchItem);
    }

}
