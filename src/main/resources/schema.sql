DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS film_reviews CASCADE;
DROP TABLE IF EXISTS reviews_likes CASCADE;
DROP TABLE IF EXISTS feed CASCADE;

CREATE TABLE IF NOT EXISTS mpa_rating
(
    rating_id INT AUTO_INCREMENT,
    name      VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS films
(
    film_id     INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    releaseDate DATE         NOT NULL,
    duration    INT,
    rating_id   INT
);



CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(100) NOT NULL,
    login    VARCHAR(50)  NOT NULL,
    name     VARCHAR(100),
    birthday DATE
);


CREATE TABLE IF NOT EXISTS friendship
(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);




CREATE TABLE IF NOT EXISTS likes
(
    film_id INT,
    user_id INT,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255)
);



CREATE TABLE IF NOT EXISTS film_genres
(
    id       INT AUTO_INCREMENT,
    film_id  INT,
    genre_id INT,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS film_reviews
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id     INT,
    user_id     INT,
    content     VARCHAR,
    is_positive BOOLEAN,
    useful      INT,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
    review_id INT,
    user_id INT,
    is_positive BOOLEAN,
    PRIMARY KEY (user_id, review_id),
    FOREIGN KEY (review_id) REFERENCES film_reviews (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors (
director_id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id BIGINT NOT NULL,
    director_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, director_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES directors(director_id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS feed (
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    entity_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);