package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;
    private final DirectorDbStorage directorStorage;

    @Override
    public Film add(Film film) {
        final String sql = "insert into films(name, description, release_date, duration, mpa_id) values (?, ?, ?, ?, ?)";
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
        addDirector(film);
        log.info("Фильм добавлен");
        return film;
    }

    @Override
    public void remove(Integer id) {
        String sql = "delete from films where id = ?";
        int updateCount = jdbcTemplate.update(sql, id);
        if (updateCount <= 0) {
            throw new NotFoundException("Фильма не существует. Удаление не возможно.");
        }
        log.info("Фильм удалён");
    }

    @Override
    public Optional<Film> update(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?";
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
            return Optional.empty();
        } else {
            addGenre(film);
            addDirector(film);
            log.info("Фильм изменён");
            return Optional.of(film);
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "select f.*, r.name mpa_name from films f " +
                "join mpa r on f.mpa_id = r.id";
        List<Film> all = jdbcTemplate.query(sql, this::mapRowToFilm);
        genreStorage.getGenresForFilms(all);
        directorStorage.getDirectorForFilms(all);
        return all;
    }

    @Override
    public Optional<Film> getById(int id) {
        try {
            String sql = "select f.*, r.name mpa_name from films f " +
                    "join mpa r on f.mpa_id = r.id " +
                    "where f.id = ?";
            List<Film> film = List.of(Objects.requireNonNull(jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id)));
            genreStorage.getGenresForFilms(film);
            directorStorage.getDirectorForFilms(film);
            return Optional.ofNullable(film.get(0));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
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
        String sql = "delete from film_likes where film_id = ?";
        jdbcTemplate.update(sql, filmId);
        log.info("Лайк от пользователя - {} удалён.", userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "select f.*, r.name mpa_name, count(fl.film_id) likes_count from films f " +
                "left join film_likes fl on f.id = fl.film_id " +
                "left join mpa r on f.mpa_id = r.id " +
                "group by f.id order by likes_count desc limit ?";
        List<Film> all = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        genreStorage.getGenresForFilms(all);
        directorStorage.getDirectorForFilms(all);
        return all;
    }

    @Override
    public List<Film> getFilmsByYear(Integer id) {
        String sql = "select f.*, r.name mpa_name from films f " +
                "left join film_directors fd on f.id = fd.film_id " +
                "left join mpa r on f.mpa_id = r.id " +
                "where fd.director_id = ? " +
                "group by f.id order by f.release_date";
        List<Film> all = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        genreStorage.getGenresForFilms(all);
        directorStorage.getDirectorForFilms(all);
        return all;
    }

    @Override
    public List<Film> getFilmsByLikes(Integer id) {
        String sql = "select f.*, r.name mpa_name, count(fl.film_id) likes_count from films f " +
                "left join film_likes fl on f.id = fl.film_id " +
                "left join film_directors fd on f.id = fd.film_id " +
                "left join mpa r on f.mpa_id = r.id " +
                "where fd.director_id = ? " +
                "group by f.id order by likes_count desc";
        List<Film> all = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        genreStorage.getGenresForFilms(all);
        directorStorage.getDirectorForFilms(all);
        return all;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getLong("duration"),
                new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")));
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

    private void addDirector(Film film) {
        if (film.getDirectors() != null) {
            String sql = "delete from film_directors where film_id = ?";
            jdbcTemplate.update(sql, film.getId());
            log.info("Режиссёры фильма удалёны.");

            List<Director> directors = film.getDirectors().stream()
                    .distinct()
                    .collect(Collectors.toList());
            film.setDirectors(directors);
            this.jdbcTemplate.batchUpdate("insert into film_directors (film_id, director_id) values(?,?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement stmt, int i) throws SQLException {
                            stmt.setInt(1, film.getId());
                            stmt.setInt(2, directors.get(i).getId());
                        }

                        public int getBatchSize() {
                            return directors.size();
                        }
                    });
        }
    }
}
