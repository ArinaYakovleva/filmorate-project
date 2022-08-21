package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.like.ILikeStorage;
import ru.yandex.practicum.filmorate.storage.recommendation.IRecommendationStorage;
import ru.yandex.practicum.filmorate.testUtils.FilmGenerator;
import ru.yandex.practicum.filmorate.testUtils.UserGenerator;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserStorageTest {

    private final IUserStorage userStorage;
    private final IFilmStorage filmStorage;
    private final ILikeStorage likeStorage;
    private final IRecommendationStorage recommendationStorage;

    @Test
    public void testAdd() {
        // Arrange
        User userIn = UserGenerator.getUser();

        // Act
        User userOut = userStorage.add(userIn);

        // Assert
        assertThat(userOut).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testEdit() {
        // Arrange
        User userIn = UserGenerator.getUser();
        userStorage.add(userIn);

        userIn.setName("test Edit Name");
        userIn.setLogin("test Edit Login");
        userIn.setBirthday(LocalDate.now().minusYears(2));
        userIn.setEmail("editEmail@mail.ru");

        // Act
        User userOut = userStorage.edit(userIn);

        // Assert
        assertThat(userOut)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "test Edit Name")
                .hasFieldOrPropertyWithValue("login", "test Edit Login")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.now().minusYears(2))
                .hasFieldOrPropertyWithValue("email", "editEmail@mail.ru");
    }

    @Test
    public void testGetOne() {
        // Arrange
        User userIn = UserGenerator.getUser();
        userStorage.add(userIn);

        // Act
        User userOut = userStorage.getOne(1L);

        // Assert
        assertThat(userOut).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(userOut).hasFieldOrPropertyWithValue("name", userIn.getName());
        assertThat(userOut).hasFieldOrPropertyWithValue("login", userIn.getLogin());
        assertThat(userOut).hasFieldOrPropertyWithValue("birthday", userIn.getBirthday());
        assertThat(userOut).hasFieldOrPropertyWithValue("email", userIn.getEmail());
    }

    @Test
    public void testGetAll() {
        // Arrange
        var users = UserGenerator.getUsers(2);
        userStorage.add(users.get(0));
        userStorage.add(users.get(1));

        // Act
        List<User> userList = userStorage.getAll();

        // Assert
        assertThat(userList.get(0))
                .hasFieldOrPropertyWithValue("id", 1L);
        assertThat(userList.get(1))
                .hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    public void testRecommendations() {
        // Arrange
        var users = UserGenerator.getUsers(3);
        var user1 = users.get(0);
        var user2 = users.get(1);
        var user3 = users.get(2);

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);

        var films = FilmGenerator.generateFilm(4);
        var film1 = films.get(0);
        var film2 = films.get(1);
        var film3 = films.get(2);
        var film4 = films.get(3);

        filmStorage.add(film1);
        filmStorage.add(film2);
        filmStorage.add(film3);
        filmStorage.add(film4);

        likeStorage.addLike(film1.getId(), user1.getId());
        likeStorage.addLike(film2.getId(), user1.getId());

        likeStorage.addLike(film1.getId(), user2.getId());
        likeStorage.addLike(film3.getId(), user2.getId());

        likeStorage.addLike(film4.getId(), user3.getId());

        // Act
        var actualFilms = recommendationStorage.getRecommendations(user1.getId());

        // Assert
        Assertions.assertEquals(1, actualFilms.size());
        Assertions.assertEquals(film3.getId(), actualFilms.get(0).getId());
    }
}