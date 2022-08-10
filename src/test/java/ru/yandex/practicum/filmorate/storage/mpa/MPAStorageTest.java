package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MPAStorageTest {

    private final MPAStorage mpaStorage;

    @Test
    public void testMPAById() {
        MPARating mpaRating = mpaStorage.getOne(1L);
        assertThat(mpaRating).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGenreAll() {
        List<MPARating> listMPA = mpaStorage.getAll();
        assertThat(listMPA.size()).isEqualTo(5);
        assertThat(listMPA.get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
        assertThat(listMPA.get(1))
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "PG");
    }

}