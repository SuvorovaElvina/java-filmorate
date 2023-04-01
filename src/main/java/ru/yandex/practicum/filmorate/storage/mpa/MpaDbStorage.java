package ru.yandex.practicum.filmorate.storage.mpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final Logger log = LoggerFactory.getLogger(GenreDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "select * from mpa";
        log.info("Получение списка рейтинга.");
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        try {
            String sql = "select * from mpa where id = ?";
            log.info("Получение рейтинга с id = {}.", id);
            return Optional.of(jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
