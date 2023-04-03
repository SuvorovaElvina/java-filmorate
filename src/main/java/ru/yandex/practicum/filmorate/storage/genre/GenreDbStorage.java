package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "select * from genres";
        log.info("Получение списка жанров.");
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getById(int id) {
        try {
            String sql = "select * from genres where id = ?";
            log.info("Получение жанра с id = {}.", id);
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
