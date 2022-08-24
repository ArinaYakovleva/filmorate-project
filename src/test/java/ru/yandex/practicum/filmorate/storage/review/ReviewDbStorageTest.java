package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.IUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReviewDbStorageTest {
    private Review goodReview;
    private Review badReview;
    private final IReviewStorage reviewStorage;
    private final IUserStorage userStorage;
    private final IFilmStorage filmStorage;

    @BeforeEach
    public void init() {
        User user = new User(1L,
                "user@mail.ru",
                "user",
                "name",
                LocalDate.of(2000, 1, 1));
        userStorage.add(user);

        Film film = new Film.FilmBuilder(null)
                .withName("test film")
                .withDescription("description")
                .withReleaseDate(LocalDate.of(2000, 1, 1))
                .withDuration(120)
                .withRate(1L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(new HashSet<>())
                .build();

        Film film1 = new Film.FilmBuilder(null)
                .withName("test bad film")
                .withDescription("description")
                .withReleaseDate(LocalDate.of(2000, 1, 1))
                .withDuration(120)
                .withRate(1L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(new HashSet<>())
                .build();

        userStorage.add(user);
        filmStorage.add(film);
        filmStorage.add(film1);

        goodReview = new Review(
                "Very bad film",
                false,
                1L,
                2L,
                1L,
                0L);

        badReview = new Review(
                "Very bad film",
                false,
                1L,
                1L,
                2L,
                0L);

        reviewStorage.add(goodReview);
        reviewStorage.add(badReview);
    }

    @Test
    public void createReview() {
        Review review = reviewStorage.add(new Review(
                "test review",
                true,
                1L,
                2L,
                3L,
                0L
        ));
        List<Review> allReviews = reviewStorage.getAll(null, 10);

        Assertions.assertEquals(3L, review.getReviewId());
        Assertions.assertEquals(3, allReviews.size());
    }

    @Test
    public void editReview() {
        badReview.setReviewId(1L);
        badReview.setFilmId(2L);

        Review updatedReview = reviewStorage.edit(badReview);
        Assertions.assertEquals(badReview, updatedReview);
    }

    @Test
    public void getAllReviews() {
        List<Review> reviewList = reviewStorage.getAll(null, 10);
        Assertions.assertEquals(2, reviewList.size());

        List<Review> reviewList2 = reviewStorage.getAll(null, 1);
        Assertions.assertEquals(1, reviewList2.size());

        List<Review> reviewList3 = reviewStorage.getAll(1L, 10);
        Assertions.assertEquals(1, reviewList3.size());

        List<Review> reviewList4 = reviewStorage.getAll(2L, 2);
        Assertions.assertEquals(1, reviewList4.size());
    }

    @Test
    public void getReview() {
        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(goodReview, review);
    }

    @Test
    public void deleteReview() {
        reviewStorage.delete(1L);

        List<Review> reviews = reviewStorage.getAll(null, 10);
        Assertions.assertEquals(1, reviews.size());
    }

    @Test
    public void likeReview() {
        reviewStorage.like(1L, 1L, true);

        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(1, review.getUseful());
    }

    @Test
    public void dislikeReview() {
        reviewStorage.like(1L, 1L, false);

        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(-1, review.getUseful());
    }

    @Test
    public void deleteLikeReview() {
        reviewStorage.like(1L, 1L, true);
        reviewStorage.deleteLike(1L, 1L, true);

        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(0, review.getUseful());
    }

    @Test
    public void deleteDislikeReview() {
        reviewStorage.like(1L, 1L, false);
        reviewStorage.deleteLike(1L, 1L, false);

        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(0, review.getUseful());
    }
}
