package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("userDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private int EVENT_ID = 0;

    @Override
    public User add(User user) {
        final String sql = "insert into users(login, name, email, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKeyAs(Integer.class));
        log.info("Пользователь добавлен");
        return user;
    }

    @Override
    public void remove(Integer id) {
        String sql = "delete from users where id = ?";
        int updateCount = jdbcTemplate.update(sql, id);
        if (updateCount <= 0) {
            throw new NotFoundException("Фильма не существует. Удаление невозможно.");
        }
        log.info("Пользователь удалён");
    }

    @Override
    public Optional<User> update(User user) {
        String sql = "update users set login = ?, name = ?, email = ?, birthday = ? where id = ?";
        int updateCount = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setInt(5, user.getId());
            return stmt;
        });
        if (updateCount <= 0) {
            return Optional.empty();
        } else {
            log.info("Пользователь изменён");
            return Optional.of(user);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public Optional<User> getById(Integer id) {
        try {
            String sql = "select * from users where id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        final String sql = "insert into friends (user_id, friend_id) values(?,?)";
        this.jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement stmt, int i) throws SQLException {
                        stmt.setInt(1, userId);
                        stmt.setInt(2, friendId);
                    }

                    @Override
                    public int getBatchSize() {
                        return 1;
                    }
                });
        log.info("Пользователь {} добавил пользователя {} в друзья.", userId, friendId);
    }

    @Override
    public List<User> getFriends(Integer id) {
        String sql = "select u.* from users u join friends f on u.id = f.friend_id where f.user_id = ?";
        log.info("Получен список друзей пользователя {}", id);
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sql = "select u.* from users u where u.id in (select friend_id from friends f " +
                "where f.user_id = ? or f.user_id = ? " +
                "group by friend_id having count(friend_id) >= 2)";
        log.info("Получен список общих друзей пользователя {} и {}", id, otherId);
        return jdbcTemplate.query(sql, this::mapRowToUser, id, otherId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Пользователь {} - удалил из друзей пользователя {}.", userId, friendId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());
    }

    @Override
    public void createFeed(int userId, String eventType, String operation, int entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        int eventId = getEventId();
        String sql = "INSERT INTO feed (userId, timestamp, eventType, operation, entityId, eventId) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, timestamp, eventType, operation, entityId, eventId);
    }

    public void createFriendFeedback(int userId, int friendId, String feedbackType, int entityId) {
        long timestamp = Timestamp.from(Instant.now()).getTime();
        String sql = "INSERT INTO user_feedback (user_id, friend_id, feedback_type, entity_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, feedbackType, entityId, timestamp);
    }

    public List<Map<String, Object>> getFriendFeedback(int userId) {
        String sql = "SELECT * FROM user_feedback WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?) AND feedback_type = 'review'";
        return jdbcTemplate.queryForList(sql, userId);
    }
    public List<Map<String, Object>> getFriendLikes(int userId) {
        String sql = "SELECT * FROM user_feedback WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?) AND feedback_type = 'like'";
        return jdbcTemplate.queryForList(sql, userId);
    }

    @Override
    public List<String> getUserFeed(Integer id) {
        String sql = "SELECT timestamp, userId, eventType, operation, entityId FROM feed WHERE userId = ?";
        List<String> feed = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        for (Map<String, Object> row : rows) {
            String eventType = row.get("eventType").toString();
            String operation = row.get("operation").toString();
            String entityId = row.get("entityId").toString();
            String timestamp = row.get("timestamp").toString();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Event Type: ").append(eventType);
            stringBuilder.append(", Operation: ").append(operation);
            stringBuilder.append(", Entity ID: ").append(entityId);
            stringBuilder.append(", Timestamp: ").append(timestamp);
            feed.add(stringBuilder.toString());
        }
        return feed;
    }

    private Integer getEventId(){
        return EVENT_ID += 2;
    }
}
