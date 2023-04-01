package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public Film add(Film film) {
        final String sql = "insert into films(name, description, releaseDate, duration, mpa_id) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));
        addGenre(film);
        log.info("Фильм добавлен");
        return film;
    }

    @Override
    public void remove(Integer id) {
        String sql = "delete from films where id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Фильм удалён");
    }

    @Override
    public Film update(Film film) {
        String sql = "update films set name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? where id = ?";
        int updateCount = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setInt(6, film.getId());
            return stmt;
        });
        if (updateCount <= 0) {
            throw new NotFoundException("Такого фильма нет в списке зарегистрированых.");
        }
        addGenre(film);
        log.info("Фильм изменён");
        return film;
    }

    @Override
    public List<Film> getAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getById(int id) {
        try {
            String sql = "select * from films where id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Такого фильма нет в списке зарегистрированых.");
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        getById(filmId);
        userDbStorage.getById(userId);
        final String sql = "insert into film_likes (film_id, user_id) values(?,?)";
        this.jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement stmt, int i) throws SQLException {
                        stmt.setInt(1, filmId);
                        stmt.setInt(2, userId);
                    }

                    @Override
                    public int getBatchSize() {
                        return 1;
                    }
                });
        log.info("Лайк от пользователя - {}.", userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        getById(filmId);
        userDbStorage.getById(userId);
        String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, filmId);
        log.info("Лайк от пользователя - {} удалён.", userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "select t.id, t.name, t.description, t.releaseDate, t.duration, t.mpa_id from " +
                "(select f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, count(fl.FILM_ID) likes_count " +
                "from films f, film_likes fl " +
                "where f.id = fl.film_id union " +
                "select f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, 0 likes_count " +
                "from films f where f.id not in " +
                "(select film_id from film_likes group by film_id) order by likes_count desc) t limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("releaseDate").toLocalDate(),
                resultSet.getLong("duration"),
                createMpa(resultSet.getInt("mpa_id")), createSetGenres(resultSet.getInt("id")));
    }

    private Mpa createMpa(Integer id) {
        String sql = "select name from mpa where id = ?";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Mpa(id, rs.getString("name"));
            }
        }, id);
    }

    private List<Genre> createSetGenres(Integer id) {
        String sql = "select g.* from genres g, film_genres fg where g.id = fg.genre_id and fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(
                        rs.getInt("id"),
                        rs.getString("name")), id);
    }

    private void addGenre(Film film) {
        if (film.getGenres() != null) {
            String sql = "delete from film_genres where film_id = ?";
            jdbcTemplate.update(sql, film.getId());
            log.info("Жанры фильма удалёны.");

            List<Genre> genres = film.getGenres().stream()
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenres(genres);
            this.jdbcTemplate.batchUpdate("insert into film_genres (film_id, genre_id) values(?,?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement stmt, int i) throws SQLException {
                            stmt.setInt(1, film.getId());
                            stmt.setInt(2, genres.get(i).getId());
                        }

                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }
}