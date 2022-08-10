package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserStorageTest {

    private final UserStorage userStorage;

    @Test
    public void testAdd() {
        User userIn = new User(null, "test@google.com", "test Login", "test Name", LocalDate.now().minusDays(10));
        User userOut = userStorage.add(userIn);
        assertThat(userOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testEdit() {
        User userIn = new User(1L, "test@google.com", "test Login", "test Name", LocalDate.now().minusDays(10));
        userStorage.add(userIn);
        userIn.setName("test Edit Name");
        User userOut = userStorage.edit(userIn);
        assertThat(userOut)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "test Edit Name");
    }

    @Test
    public void testGetOne() {
        User userIn = new User(null, "test@google.com", "test Login", "test Name", LocalDate.now().minusDays(10));
        userStorage.add(userIn);
        User userOut = userStorage.getOne(1L);
        assertThat(userOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGetAll() {
        User userIn1 = new User(null, "test1@google.com", "test Login 1", "test Name 1", LocalDate.now().minusDays(10));
        userStorage.add(userIn1);
        User userIn2 = new User(null, "test2@google.com", "test Login 2", "test Name 2", LocalDate.now().minusDays(10));
        userStorage.add(userIn2);
        List<User> userList = userStorage.getAll();
        assertThat(userList.get(0))
                .hasFieldOrPropertyWithValue("id", 1L);
        assertThat(userList.get(1))
                .hasFieldOrPropertyWithValue("id", 2L);
    }
}