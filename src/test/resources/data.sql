DELETE FROM FILM_GENRE;
DELETE FROM FILM_LIKES;
DELETE FROM FRIENDS;
DELETE FROM USERS;
DELETE FROM FILMS;

ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH 1;

MERGE INTO RATING (id, name, description)
VALUES
    (1,'G','без ограничений'),
    (2,'PG','детям рекомендуется смотреть фильм с родителями'),
    (3,'PG-13','детям до 13 лет просмотр не желателен'),
    (4,'R','лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
    (5,'NC-17','лицам до 18 лет просмотр запрещён');

MERGE INTO GENRES (id, name)
VALUES
    (1,'Комедия'),
    (2,'Драма'),
    (3,'Мультфильм'),
    (4,'Триллер'),
    (5,'Документальный'),
    (6,'Боевик');

-- Тесты для режиссера
MERGE INTO DIRECTOR (id, name)
    VALUES
        (1,'Квентин Тарантино'),
        (2,'Стивен Спилберг');
