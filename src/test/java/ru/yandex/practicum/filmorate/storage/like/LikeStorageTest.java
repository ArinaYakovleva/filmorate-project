package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class LikeStorageTest {

    private final ILikeStorage likeStorage;

    @Test
    public void testLikeAddThrows() {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> likeStorage.addLike(1L, 1L));
    }

    @Test
    public void testLikeDelete() {
        //без Mock пока проверить ничего не получается
        likeStorage.deleteLike(-1L, -1L);
    }

}