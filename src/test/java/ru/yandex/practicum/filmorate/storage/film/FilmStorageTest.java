package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FilmStorageTest {

    private final FilmStorage filmStorage;

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

}