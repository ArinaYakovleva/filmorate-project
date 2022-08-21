package ru.yandex.practicum.filmorate.storage.friends;

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
class FriendsStorageTest {

    private final IFriendsStorage friendsStorage;

    @Test
    public void testAddFriend() {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> friendsStorage.addFriend(1L, 1L));
    }

    @Test
    public void testDeleteFriend() {
        //без Mock пока проверить ничего не получается
        //friendsStorage.deleteFriend(-1L, -1L);
    }
}

