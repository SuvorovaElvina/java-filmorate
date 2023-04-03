package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

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
