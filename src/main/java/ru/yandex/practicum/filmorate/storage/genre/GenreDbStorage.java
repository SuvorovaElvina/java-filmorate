package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    public void getGenresForFilms(List<Film> films) {
        Map<Integer, List<Genre>> genres = new LinkedHashMap<>();
        List<Integer> id = new ArrayList<>();
        for (Film film : films) {
            id.add(film.getId());
        }
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sql = "select fg.film_id, g.* from film_genres fg " +
                "join genres g on g.id = fg.genre_id " +
                "where fg.film_id in (%s)";
        jdbcTemplate.query(String.format(sql, inSql), rs -> {
            Integer filmId = rs.getInt("film_id");
            genres.putIfAbsent(filmId, new ArrayList<>());
            Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
            genres.get(filmId).add(genre);
        }, id.toArray());
        for (Film film : films) {
            if (genres.get(film.getId()) != null) {
                film.setGenres(genres.get(film.getId()));
            }
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
