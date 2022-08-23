CREATE TABLE IF NOT EXISTS rating
(
    id          bigint,
    name        varchar NOT NULL,
    description varchar,
    CONSTRAINT pk_rating PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS director
(
    id   bigint AUTO_INCREMENT,
    name character varying NOT NULL,
    CONSTRAINT director_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS films
(
    id           bigint PRIMARY KEY AUTO_INCREMENT,
    name         varchar(200) NOT NULL,
    rate         bigint,
    rating_id    bigint       NOT NULL,
    description  varchar,
    release_date timestamp,
    duration     int          NOT NULL,
    director_id  bigint,
    CONSTRAINT fk_films_rating_id FOREIGN KEY (rating_id) REFERENCES rating (id),
    CONSTRAINT films_director_id_fk FOREIGN KEY (director_id) REFERENCES director (id)
);

CREATE TABLE IF NOT EXISTS genres
(
    id   bigint,
    name varchar NOT NULL,
    CONSTRAINT pk_genres PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  bigint,
    genre_id bigint,
    CONSTRAINT pk_film_genre PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genre_film_id FOREIGN KEY (film_id) REFERENCES films (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_film_genre_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    id       bigint PRIMARY KEY AUTO_INCREMENT,
    name     varchar,
    login    varchar NOT NULL,
    email    varchar,
    birthday timestamp
);

CREATE TABLE IF NOT EXISTS film_likes
(
    user_id bigint,
    film_id bigint,
    CONSTRAINT pk_film_likes PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_film_likes_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_film_likes_film_id FOREIGN KEY (film_id) REFERENCES films (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_id     bigint NOT NULL,
    director_id bigint NOT NULL,
    CONSTRAINT pk_film_director
        PRIMARY KEY (film_id, director_id),
    CONSTRAINT fk_film_director_film_id
        FOREIGN KEY (film_id) REFERENCES films
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_film_director_genre_id
        FOREIGN KEY (director_id) REFERENCES director
            ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   bigint,
    friend_id bigint,
    status    boolean NOT NULL,
    CONSTRAINT pk_friends PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friends_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_friends_friend_id FOREIGN KEY (friend_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   bigint PRIMARY KEY AUTO_INCREMENT,
    content     varchar,
    is_positive boolean,
    user_id     bigint,
    film_id     bigint,
    CONSTRAINT fk_users_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_films_film_id FOREIGN KEY (film_id) REFERENCES films (id)
);

CREATE TABLE IF NOT EXISTS rating_reviews
(
    user_id     bigint,
    review_id   bigint,
    is_positive boolean,
    CONSTRAINT pk_ratings_of_reviews PRIMARY KEY (review_id, user_id),
    CONSTRAINT fk_ratings_of_reviews FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_review_id FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_feed
(
    event_id   bigint PRIMARY KEY AUTO_INCREMENT,
    user_id    bigint,
    event_type varchar,
    operation  varchar,
    entity_id  bigint,
    timestamp  timestamp,
    CONSTRAINT fk_event_feed_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);