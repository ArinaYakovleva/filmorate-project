package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FilmStorageTest {

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    @Test
    public void testAddFilm() {
        Film filmIn = new Film(null, "test name", "test description", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        Film filmOut = filmStorage.add(filmIn);
        assertThat(filmOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testEditFilm() {
        Film filmIn = new Film(null, "test name", "test description", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn);
        filmIn.setName("New Edit Film");
        Film filmOut = filmStorage.edit(filmIn);
        assertThat(filmOut)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "New Edit Film");
    }

    @Test
    public void testGetAll() {
        Film filmIn = new Film(null, "test name 1", "test description 1", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn);
        Film filmIn2 = new Film(null, "test name 2", "test description 2", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn2);
        List<Film> filmList = filmStorage.getAll();
        assertThat(filmList.get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "test name 1");
        assertThat(filmList.get(1))
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "test name 2");

    }

    @Test
    public void testGetOne() {
        Film filmIn = new Film(null, "test name", "test description", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn);
        Film filmOut = filmStorage.getOne(1L);
        assertThat(filmOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGetPopularFilms() {
        Film filmIn = new Film(null, "test name 1", "test description 1", LocalDate.now(), 120, 2L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn);
        Film filmIn2 = new Film(null, "test name 2", "test description 2", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn2);
        List<Film> filmList = filmStorage.getPopularFilms(2);
        assertThat(filmList.get(0))
                .hasFieldOrPropertyWithValue("id", 2L);
        assertThat(filmList.get(1))
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void getOrderedFilmsByYear() {
        Director director = directorStorage.getOne(1L);

        // Поздний фильм
        Film filmIn = new Film(null, "test name 1", "test description 1", LocalDate.now(), 120, 2L, new MPARating(1L, "G", "без ограничений"));
        filmIn.setDirectors(Set.of(director));
        filmStorage.add(filmIn);

        // Ранний фильм
        Film filmIn2 = new Film(null, "test name 2", "test description 2", LocalDate.now().minusDays(1), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmIn2.setDirectors(Set.of(director));
        filmStorage.add(filmIn2);

        // Фильм другого режиссера
        Film filmIn3 = new Film(null, "test name 2", "test description 2", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn3);

        // Список фильмов режиссера, отсортированный по году выхода
        List<Film> filmsOrderedByYear = filmStorage.getDirectorFilms(1L, "year");

        assertThat(filmsOrderedByYear.size()).isEqualTo(2);
        assertThat(filmsOrderedByYear.get(0).getId()).isEqualTo(2);
        assertThat(filmsOrderedByYear.get(1).getId()).isEqualTo(1);
    }

    @Test
    void getOrderedFilmsByLikesCount() {
        Director director = directorStorage.getOne(1L);
        User userOne = userStorage.add(new User(1L, "emailOne@mail.com", "loginOne", "NameOne", LocalDate.now().minusDays(1)));
        User userTwo = userStorage.add(new User(2L, "emailTwo@mail.com", "loginOne", "NameTwo", LocalDate.now().minusDays(1)));

        // Непопулярный фильм
        Film filmIn = new Film(null, "test name 1", "test description 1", LocalDate.now(), 120, 2L, new MPARating(1L, "G", "без ограничений"));
        filmIn.setDirectors(Set.of(director));
        filmStorage.add(filmIn);
        likeStorage.addLike(filmIn.getId(), userOne.getId());

        // Популярный фильм
        Film filmIn2 = new Film(null, "test name 2", "test description 2", LocalDate.now().minusDays(1), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmIn2.setDirectors(Set.of(director));
        filmStorage.add(filmIn2);
        likeStorage.addLike(filmIn2.getId(), userOne.getId());
        likeStorage.addLike(filmIn2.getId(), userTwo.getId());

        // Фильм другого режиссера
        Film filmIn3 = new Film(null, "test name 2", "test description 2", LocalDate.now(), 120, 4L, new MPARating(1L, "G", "без ограничений"));
        filmStorage.add(filmIn3);
        likeStorage.addLike(filmIn3.getId(), userOne.getId());
        likeStorage.addLike(filmIn3.getId(), userTwo.getId());

        // Список фильмов режиссера, отсортированный по количеству лайков
        List<Film> filmsOrderedByLikesCount = filmStorage.getDirectorFilms(1L, "year");

        assertThat(filmsOrderedByLikesCount.size()).isEqualTo(2);
        assertThat(filmsOrderedByLikesCount.get(0).getId()).isEqualTo(2);
        assertThat(filmsOrderedByLikesCount.get(1).getId()).isEqualTo(1);
    }

}