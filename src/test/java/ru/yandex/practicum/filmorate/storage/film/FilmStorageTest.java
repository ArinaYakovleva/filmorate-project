package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.IDirectorStorage;
import ru.yandex.practicum.filmorate.storage.like.ILikeStorage;
import ru.yandex.practicum.filmorate.storage.user.IUserStorage;
import ru.yandex.practicum.filmorate.testUtils.FilmGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FilmStorageTest {

    private final IFilmStorage filmStorage;
    private final IDirectorStorage directorStorage;

    private final ILikeStorage likeStorage;
    private final IUserStorage userStorage;

    @Test
    public void testAddFilm() {
        // Arrange
        var filmIn = FilmGenerator.generateFilm();

        // Act
        Film filmOut = filmStorage.add(filmIn);

        // Assert
        assertThat(filmOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testEditFilm() {
        // Arrange
        var filmIn = FilmGenerator.generateFilm();
        filmStorage.add(filmIn);

        filmIn.setName("New Edit Film");

        // Act
        Film filmOut = filmStorage.edit(filmIn);

        // Assert
        assertThat(filmOut)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "New Edit Film");
    }

    @Test
    public void testGetAll() {
        // Arrange
        var films = FilmGenerator.generateFilm(2);
        filmStorage.add(films.get(0));
        filmStorage.add(films.get(1));

        // Act
        List<Film> filmList = filmStorage.getAll();

        // Assert
        assertThat(filmList.get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", films.get(0).getName());
        assertThat(filmList.get(1))
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", films.get(1).getName());
    }

    @Test
    public void testGetOne() {
        // Arrange
        var filmIn = FilmGenerator.generateFilm();
        filmStorage.add(filmIn);

        // Act
        Film filmOut = filmStorage.getOne(1L);

        // Assert
        assertThat(filmOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGetPopularFilmsWithCount() {
        // Arrange
        var films = FilmGenerator.generateFilm(2);
        filmStorage.add(films.get(0));
        filmStorage.add(films.get(1));
        User userIn1 = new User(null, "test1@google.com", "test Login 1", "test Name 1", LocalDate.now().minusDays(10));
        userStorage.add(userIn1);
        User userIn2 = new User(null, "test2@google.com", "test Login 2", "test Name 2", LocalDate.now().minusDays(10));
        userStorage.add(userIn2);
        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(2L, 1L);
        likeStorage.addLike(2L, 2L);
        // Act
        List<Film> filmList = filmStorage.getPopularFilms(2, null, null);

        // Assert
        assertThat(filmList.get(0))
                .hasFieldOrPropertyWithValue("id", 2L);
        assertThat(filmList.get(1))
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGetPopularFilmsWithGenreId() {
        // Arrange
        var films = FilmGenerator.generateFilm(2);

        films.get(0).setGenres(Set.of(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма")));
        films.get(1).setGenres(Set.of(new Genre(3L, "Мультфильм")));

        filmStorage.add(films.get(0));
        filmStorage.add(films.get(1));

        films.get(0).setId(1L);

        // Act
        List<Film> actualFilms = filmStorage.getPopularFilms(2, 1L, null);

        // Assert
        Assertions.assertEquals(1, actualFilms.size());

        Assertions.assertEquals(films.get(0).getGenres(), actualFilms.get(0).getGenres());
    }

    @Test
    public void testGetPopularFilmsYear() {
        // Arrange
        var films = FilmGenerator.generateFilm(2);
        int expectedYear = 2000;

        films.get(0).setReleaseDate(LocalDate.of(expectedYear, 1, 1));
        films.get(1).setReleaseDate(LocalDate.of(2022, 1, 1));

        filmStorage.add(films.get(0));
        filmStorage.add(films.get(1));

        films.get(0).setId(1L);

        // Act
        List<Film> actualFilms = filmStorage.getPopularFilms(2, null, expectedYear);

        // Assert
        Assertions.assertTrue(
                actualFilms.stream()
                        .allMatch(f -> f.getReleaseDate().getYear() == expectedYear));
    }

    @Test
    void getOrderedFilmsByYear() {
        // Arrange
        Director director = directorStorage.getOne(1L);

        // Поздний фильм
        Film filmIn = new Film.FilmBuilder(null)
                .withName("test name 1")
                .withDescription("test description 1")
                .withReleaseDate(LocalDate.now())
                .withDuration(120)
                .withRate(2L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(Set.of(director))
                .build();

        filmStorage.add(filmIn);

        // Ранний фильм
        Film filmIn2 = new Film.FilmBuilder(null)
                .withName("test name 2")
                .withDescription("test description 2")
                .withReleaseDate(LocalDate.now().minusDays(1))
                .withDuration(120)
                .withRate(4L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(Set.of(director))
                .build();

        filmStorage.add(filmIn2);

        // Фильм другого режиссера
        Film filmIn3 = new Film.FilmBuilder(null)
                .withName("test name 2")
                .withDescription("test description 2")
                .withReleaseDate(LocalDate.now())
                .withDuration(120)
                .withRate(4L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .build();

        filmStorage.add(filmIn3);

        // Act (Список фильмов режиссера, отсортированный по году выхода)
        List<Film> filmsOrderedByYear = filmStorage.getDirectorFilms(1L, "year");

        // Assert
        assertThat(filmsOrderedByYear.size()).isEqualTo(2);
        assertThat(filmsOrderedByYear.get(0).getId()).isEqualTo(2);
        assertThat(filmsOrderedByYear.get(1).getId()).isEqualTo(1);
    }

    @Test
    void getOrderedFilmsByLikesCount() {
        // Arrange
        Director director = directorStorage.getOne(1L);
        User userOne = userStorage.add(new User(1L, "emailOne@mail.com", "loginOne", "NameOne", LocalDate.now().minusDays(1)));
        User userTwo = userStorage.add(new User(2L, "emailTwo@mail.com", "loginOne", "NameTwo", LocalDate.now().minusDays(1)));

        // Непопулярный фильм
        Film filmIn = new Film.FilmBuilder(null)
                .withName("test name 1")
                .withDescription("test description 1")
                .withReleaseDate(LocalDate.now())
                .withDuration(120)
                .withRate(2L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(Set.of(director))
                .build();

        filmStorage.add(filmIn);
        likeStorage.addLike(filmIn.getId(), userOne.getId());

        // Популярный фильм
        Film filmIn2 = new Film.FilmBuilder(null)
                .withName("test name 2")
                .withDescription("test description 2")
                .withReleaseDate(LocalDate.now().minusDays(1))
                .withDuration(120)
                .withRate(4L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(Set.of(director))
                .build();

        filmStorage.add(filmIn2);
        likeStorage.addLike(filmIn2.getId(), userOne.getId());
        likeStorage.addLike(filmIn2.getId(), userTwo.getId());

        // Фильм другого режиссера
        Film filmIn3 = new Film.FilmBuilder(null)
                .withName("test name 2")
                .withDescription("test description 2")
                .withReleaseDate(LocalDate.now())
                .withDuration(120)
                .withRate(4L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .build();

        filmStorage.add(filmIn3);
        likeStorage.addLike(filmIn3.getId(), userOne.getId());
        likeStorage.addLike(filmIn3.getId(), userTwo.getId());

        // Act (Список фильмов режиссера, отсортированный по количеству лайков)
        List<Film> filmsOrderedByLikesCount = filmStorage.getDirectorFilms(1L, "year");

        // Assert
        assertThat(filmsOrderedByLikesCount.size()).isEqualTo(2);
        assertThat(filmsOrderedByLikesCount.get(0).getId()).isEqualTo(2);
        assertThat(filmsOrderedByLikesCount.get(1).getId()).isEqualTo(1);
    }

    @Test
    public void getCommonFilms() {
        var filmIn = FilmGenerator.generateFilm();
        filmStorage.add(filmIn);
        userStorage.add(new User(
                1L,
                "emailOne@mail.com",
                "testOne",
                "TestOne",
                LocalDate.now().minusDays(1)));
        userStorage.add(new User(
                2L,
                "emailTwo@mail.com",
                "testTwo",
                "TestTwo",
                LocalDate.now().minusDays(1)));
        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);

        List<Film> commonFilms = filmStorage.getCommonFilms(1L, 2L);
        Assertions.assertEquals(1, commonFilms.size());
        Assertions.assertEquals(1L, commonFilms.get(0).getId());
    }
}
