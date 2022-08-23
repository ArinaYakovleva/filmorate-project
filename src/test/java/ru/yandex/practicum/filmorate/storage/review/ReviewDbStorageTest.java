package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
    private static Review goodReview;
    private final IReviewStorage reviewStorage;
    private final IUserStorage userStorage;
    private final IFilmStorage filmStorage;

    @BeforeAll
    public static void init() {
        goodReview = new Review("Very bad film", false, 1L, 2L);
        goodReview.setReviewId(2L);
        goodReview.setUseful(1L);

    }

    @BeforeEach
    public void init1() {
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
    }

    @Test
    public void createReview() {
        Review review = reviewStorage.add(goodReview);
        Assertions.assertEquals(1L, review.getReviewId());
    }

    @Test
    public void editReview() {
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        addedReview.setReviewId(1L);
        addedReview.setUseful(0L);

        reviewStorage.add(addedReview);

        Review review = new Review(
                "Very good film",
                true,
                1L,
                1L);
        review.setReviewId(1L);
        review.setUseful(0L);
        Review updatedReview = reviewStorage.edit(review);
        Assertions.assertEquals(review, updatedReview);
    }

    @Test
    public void getAllReviews() {
        reviewStorage.add(new Review(
                "Very bad film",
                false,
                1L,
                1L));
        Review newReview = new Review(
                "Very good film",
                true,
                1L,
                2L);
        newReview.setUseful(1L);
        reviewStorage.add(newReview);

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
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        addedReview.setReviewId(1L);
        addedReview.setUseful(0L);
        reviewStorage.add(addedReview);

        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(addedReview, review);
    }

    @Test
    public void deleteReview() {
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        reviewStorage.add(addedReview);
        reviewStorage.delete(1L);
        List<Review> reviews = reviewStorage.getAll(null, 10);
        Assertions.assertEquals(0, reviews.size());
    }

    @Test
    public void likeReview() {
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        reviewStorage.add(addedReview);
        reviewStorage.like(1L, 1L, true);
        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(1, review.getUseful());
    }

    @Test
    public void dislikeReview() {
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        reviewStorage.add(addedReview);
        reviewStorage.like(1L, 1L, false);
        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(-1, review.getUseful());
    }

    @Test
    public void deleteLikeReview() {
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        reviewStorage.add(addedReview);
        reviewStorage.like(1L, 1L, true);
        reviewStorage.deleteLike(1L, 1L, true);
        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(0, review.getUseful());
    }

    @Test
    public void deleteDislikeReview() {
        Review addedReview = new Review(
                "Very bad film",
                false,
                1L,
                1L);
        reviewStorage.add(addedReview);
        reviewStorage.like(1L, 1L, false);
        reviewStorage.deleteLike(1L, 1L, false);
        Review review = reviewStorage.getOne(1L);
        Assertions.assertEquals(0, review.getUseful());
    }
}
