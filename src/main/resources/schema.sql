
CREATE TABLE IF NOT EXISTS rating (
    id bigint ,
    name varchar NOT NULL ,
    description varchar,
    CONSTRAINT pk_rating PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS DIRECTOR (
    id   BIGINT AUTO_INCREMENT,
    name CHARACTER VARYING NOT NULL,
    CONSTRAINT DIRECTOR_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS films (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name varchar(200) NOT NULL ,
    rate bigint,
    rating_id bigint NOT NULL ,
    description varchar ,
    release_date timestamp ,
    duration int NOT NULL ,
    director_id  BIGINT,
    CONSTRAINT fk_films_rating_id FOREIGN KEY(rating_id) REFERENCES rating (id),
    CONSTRAINT films_director_id_fk FOREIGN KEY (director_id) REFERENCES director (id)
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
    CONSTRAINT fk_film_genre_film_id FOREIGN KEY(film_id) REFERENCES films (id) on update cascade on delete cascade,
    CONSTRAINT fk_film_genre_genre_id FOREIGN KEY(genre_id) REFERENCES genres (id) on update cascade on delete cascade
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
    CONSTRAINT fk_film_likes_user_id FOREIGN KEY(user_id) REFERENCES users (id) on update cascade on delete cascade,
    CONSTRAINT fk_film_likes_film_id FOREIGN KEY(film_id) REFERENCES films (id) on update cascade on delete cascade
);

create table if not exists FILM_DIRECTOR
(
    FILM_ID     BIGINT not null,
    DIRECTOR_ID BIGINT not null,
    constraint PK_FILM_DIRECTOR
        primary key (FILM_ID, DIRECTOR_ID),
    constraint FK_FILM_DIRECTOR_FILM_ID
        foreign key (FILM_ID) references FILMS
            on update cascade on delete cascade,
    constraint FK_FILM_DIRECTOR_GENRE_ID
        foreign key (DIRECTOR_ID) references DIRECTOR
            on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS friends (
    user_id bigint ,
    friend_id bigint  ,
    status boolean  NOT NULL ,
    CONSTRAINT pk_friends PRIMARY KEY (user_id,friend_id),
    CONSTRAINT fk_friends_user_id FOREIGN KEY(user_id) REFERENCES users (id) on update cascade on delete cascade,
    CONSTRAINT fk_friends_friend_id FOREIGN KEY(friend_id) REFERENCES users (id) on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id bigint PRIMARY KEY AUTO_INCREMENT,
    content varchar,
    is_positive boolean,
    user_id bigint,
    film_id bigint,
    CONSTRAINT fk_users_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_films_film_id FOREIGN KEY (film_id) REFERENCES films (id)
);

CREATE TABLE IF NOT EXISTS rating_reviews (
    user_id bigint,
    review_id bigint,
    is_positive boolean,
    CONSTRAINT pk_ratings_of_reviews PRIMARY KEY (review_id, user_id),
    CONSTRAINT fk_ratings_of_reviews FOREIGN KEY (user_id) REFERENCES users(id) on delete cascade,
    CONSTRAINT fk_reviews_review_id FOREIGN KEY (review_id) REFERENCES reviews(review_id) on delete cascade
);

CREATE TABLE IF NOT EXISTS event_feed (
    event_id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint,
    event_type varchar,
    operation varchar,
    entity_id bigint,
    timestamp timestamp,
    CONSTRAINT fk_event_feed_user_id FOREIGN KEY(user_id) REFERENCES users (id) on update cascade on delete cascade
);