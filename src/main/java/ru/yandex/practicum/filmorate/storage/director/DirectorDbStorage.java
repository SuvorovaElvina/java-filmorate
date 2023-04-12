package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director add(Director director) {
        final String sql = "insert into directors(name) values ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKeyAs(Integer.class));
        log.info("Режиссёр добавлен");
        return director;
    }

    @Override
    public void remove(Integer id) {
        String sql = "delete from directors where id = ?";
        int updateCount = jdbcTemplate.update(sql, id);
        if (updateCount <= 0) {
            throw new NotFoundException("Режиссёра не существует. Удаление не возможно.");
        }
        log.info("Режиссёр удалён");
    }

    @Override
    public Optional<Director> update(Director director) {
        String sql = "update directors set name = ? where id = ?";
        int updateCount = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (updateCount <= 0) {
            return Optional.empty();
        } else {
            log.info("Режиссёр изменён");
            return Optional.of(director);
        }
    }

    @Override
    public List<Director> getAll() {
        String sql = "select * from directors";
        return jdbcTemplate.query(sql, this::mapRowToDirector);
    }

    @Override
    public Optional<Director> getById(int id) {
        try {
            String sql = "select * from directors where id = ?";
            List<Director> film = List.of(Objects.requireNonNull(jdbcTemplate.queryForObject(sql, this::mapRowToDirector, id)));
            return Optional.ofNullable(film.get(0));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
