package ru.yandex.practicum.filmorate.storage.search;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.IDirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.like.ILikeStorage;
import ru.yandex.practicum.filmorate.storage.user.IUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchDbStorageTest {

    private final IFilmStorage filmStorage;
    private final IDirectorStorage directorStorage;
    private final ILikeStorage likeStorage;
    private final IUserStorage userStorage;
    //private final ISearchStorage searchStorage;

    private Film first;
    private Film second;
    private Film third;
    private Film fourth;

    @BeforeAll
    public void init() {
        Set<Director> oneDirector =
                new HashSet<>() {{
                    add(directorStorage.getOne(1L));
                }}; //Тарантино
        Set<Director> twoDirectors =
                new HashSet<>() {{
                    add(directorStorage.getOne(1L));
                    add(directorStorage.getOne(2L));
                }}; //Тарантино и Спилберг

        first = new Film.FilmBuilder(null)
                .withName("Фильм")
                .withDescription("Описание")
                .withReleaseDate(LocalDate.of(2000, 1, 1))
                .withDuration(120)
                .withRate(1L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .build();
        second = new Film.FilmBuilder(null)
                .withName("Фильм часть два")
                .withDescription("Описание")
                .withReleaseDate(LocalDate.of(2000, 1, 1))
                .withDuration(120)
                .withRate(1L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .build();
        third = new Film.FilmBuilder(null)
                .withName("Фильм про Спилберга")
                .withDescription("Описание")
                .withReleaseDate(LocalDate.of(2000, 1, 1))
                .withDuration(120)
                .withRate(1L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(oneDirector)
                .build();
        fourth = new Film.FilmBuilder(null)
                .withName("Режисерский")
                .withDescription("Описание")
                .withReleaseDate(LocalDate.of(2000, 1, 1))
                .withDuration(120)
                .withRate(1L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .withDirectors(twoDirectors)
                .build();

        filmStorage.add(first);
        filmStorage.add(second);
        filmStorage.add(third);
        filmStorage.add(fourth);

        userStorage.add(new User(1L, "email@email.com", "login1", null, LocalDate.of(1997, 10, 10)));
        userStorage.add(new User(2L, "email@email.com", "login1", null, LocalDate.of(1997, 10, 10)));
        userStorage.add(new User(3L, "email@email.com", "login1", null, LocalDate.of(1997, 10, 10)));

        //Добавил лайки в порядке уменьшения
        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);
        likeStorage.addLike(1L, 3L);
        likeStorage.addLike(2L, 2L);
        likeStorage.addLike(2L, 3L);
        likeStorage.addLike(3L, 1L);
    }


    @Test
    public void findWithName() {
        List<Film> result = filmStorage.getFilmsBySearch(null, "фильм");
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", 2L);
        assertThat(result.get(2))
                .hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    public void findWithDirector() {
        List<Film> result = filmStorage.getFilmsBySearch("спилберг", null);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    public void findWithBoth() {
        List<Film> result = filmStorage.getFilmsBySearch("спилберг", "спилберг");
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", 3L);
        assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("id", 4L);
    }

}
