
CREATE TABLE IF NOT EXISTS rating (
    id bigint ,
    name varchar NOT NULL ,
    description varchar,
    CONSTRAINT pk_rating PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS films (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name varchar(200) NOT NULL ,
    rate bigint,
    rating_id bigint NOT NULL ,
    description varchar ,
    release_date timestamp ,
    duration int NOT NULL ,
    CONSTRAINT fk_films_rating_id FOREIGN KEY(rating_id) REFERENCES rating (id)
);

CREATE TABLE IF NOT EXISTS genres (
    id bigint,
    name varchar NOT NULL,
    CONSTRAINT pk_genres PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id bigint ,
    genre_id bigint ,
    CONSTRAINT pk_film_genre PRIMARY KEY (film_id,genre_id),
    CONSTRAINT fk_film_genre_film_id FOREIGN KEY(film_id) REFERENCES films (id),
    CONSTRAINT fk_film_genre_genre_id FOREIGN KEY(genre_id) REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS users (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name varchar ,
    login varchar  NOT NULL ,
    email varchar ,
    birthday timestamp
);

CREATE TABLE IF NOT EXISTS film_likes (
    user_id bigint ,
    film_id bigint ,
    CONSTRAINT pk_film_likes PRIMARY KEY (user_id,film_id),
    CONSTRAINT fk_film_likes_user_id FOREIGN KEY(user_id) REFERENCES users (id),
    CONSTRAINT fk_film_likes_film_id FOREIGN KEY(film_id) REFERENCES films (id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id bigint ,
    friend_id bigint  ,
    status boolean  NOT NULL ,
    CONSTRAINT pk_friends PRIMARY KEY (user_id,friend_id),
    CONSTRAINT fk_friends_user_id FOREIGN KEY(user_id) REFERENCES users (id),
    CONSTRAINT fk_friends_friend_id FOREIGN KEY(friend_id) REFERENCES users (id)
);

