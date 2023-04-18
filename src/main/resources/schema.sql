DROP TABLE IF EXISTS user_feedback CASCADE;
DROP TABLE IF EXISTS feed CASCADE;
DROP TABLE IF EXISTS review_likes CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS genres CASCADE;

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);
CREATE TABLE IF NOT EXISTS mpa
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);
CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar      NOT NULL,
    description  varchar(200) NOT NULL,
    release_date date         NOT NULL,
    duration     bigint,
    mpa_id       integer REFERENCES mpa (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  integer REFERENCES films (id) ON DELETE CASCADE,
    genre_id integer REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT film_genres_PK PRIMARY KEY (film_id, genre_id)
);
CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar NOT NULL,
    login    varchar NOT NULL,
    name     varchar,
    birthday date
);
CREATE TABLE IF NOT EXISTS film_likes
(
    film_id integer REFERENCES films (id) ON DELETE CASCADE,
    user_id integer REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT film_likes_PK PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS friends
(
    user_id   integer REFERENCES users (id) ON DELETE CASCADE,
    friend_id integer REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FRIENDS_PK PRIMARY KEY (USER_ID, FRIEND_ID)
);
CREATE TABLE IF NOT EXISTS directors
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);
CREATE TABLE IF NOT EXISTS film_directors
(
    film_id     integer REFERENCES films (id) ON DELETE CASCADE,
    director_id integer REFERENCES directors (id) ON DELETE CASCADE,
    CONSTRAINT film_directors_PK PRIMARY KEY (film_id, director_id)
);
CREATE TABLE IF NOT EXISTS reviews
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content    varchar NOT NULL,
    isPositive BOOLEAN,
    user_id    INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    film_id    INTEGER NOT NULL REFERENCES films (id) ON DELETE CASCADE,
    useful     INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS review_likes
(
    review_id integer REFERENCES reviews (id) ON DELETE CASCADE,
    user_id   integer REFERENCES users (id) ON DELETE CASCADE,
    isLike    BOOLEAN,
    CONSTRAINT review_likes_PK PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS feed
(
    eventId   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    userId    integer REFERENCES users (id),
    timestamp BIGINT,
    eventType varchar NOT NULL,
    operation varchar NOT NULL,
    entityId  integer
);

CREATE TABLE IF NOT EXISTS user_feedback
(
    feedback_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id       INTEGER,
    friend_id     INTEGER,
    feedback_type VARCHAR NOT NULL,
    entity_id     INTEGER,
    timestamp     BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);