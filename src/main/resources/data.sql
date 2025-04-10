INSERT INTO Users (email, login, name, birthday)
VALUES ('user1@example.com', 'user1', 'User One', '1990-01-01'),
       ('user2@example.com', 'user2', 'User Two', '1995-05-05');

-- MPA
INSERT INTO MPA_RATING (rating_id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

-- Жанры
INSERT INTO GENRES (GENRE_ID, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

-- Фильмы
INSERT INTO FILMS (name, description, releaseDate , duration, RATING_ID)
VALUES ('Фильм 1', 'Описание 1', '2020-01-01', 120, 1),
       ('Фильм 2', 'Описание 2', '2021-01-01', 130, 2);

-- Жанры для фильмов
INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 3),
       (1, 5),
       (2, 2);