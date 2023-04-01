package ru.yandex.practicum.filmorate.storage.genre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {
    private final Logger log = LoggerFactory.getLogger(GenreDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
            return Optional.of(jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
