package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DirectorDbStorageTest {
    private final DirectorStorage directorStorage;

    @Test
    void findDirectorById() {
        Director director = directorStorage.getOne(1L);
        assertThat(director).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void findDirectorByIds() {
        List<Director> directors = directorStorage.getAll();
        assertThat(directors.size()).isEqualTo(2);
        assertThat(directors.get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Квентин Тарантино");
        assertThat(directors.get(1))
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Стивен Спилберг");
    }

    @Test
    void removeDirector() {
        List<Director> directors = directorStorage.getAll();
        assertThat(directors.size()).isEqualTo(2);

        directorStorage.remove(directors.get(0).getId());
        assertThat(directorStorage.getAll().size()).isEqualTo(1);
    }
}