package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
class FilmorateApplicationTests {

    private final GenreStorage genreStorage;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGenreById() {

        Genre genre = genreStorage.getOne(1L);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L);

    }
}
