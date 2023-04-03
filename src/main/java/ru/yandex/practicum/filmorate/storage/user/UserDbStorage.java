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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("userDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

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
        getById(id);
        String sql = "delete from users where id = ?";
        jdbcTemplate.update(sql, id);
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
        String sql = "select u.* from users u where u.id in (select friend_id from friends f where f.user_id = ?)";
        log.info("Получен список друзей пользователя {}", id);
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) { //подумать
        String sql = "select u.* from users u where u.id in (select friend_id from friends f " +
                "where f.user_id = ? or f.user_id = ? GROUP BY friend_id HAVING count(friend_id) >= 2)";
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
}
