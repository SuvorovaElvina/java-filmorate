package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component("feedDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FeedDdStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private int eventId = 0;

    @Override
    public void createFeed(int userId, String eventType, String operation, int entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        eventId += 1;
        String sql = "INSERT INTO feed (userId, timestamp, eventType, operation, entityId, eventId) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, timestamp, eventType, operation, entityId, eventId);
    }
}
