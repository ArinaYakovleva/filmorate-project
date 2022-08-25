package ru.yandex.practicum.filmorate.testUtils;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserGenerator {

    public static User getUser() {
        return new User(
                null,
                "test@google.com",
                "test Login",
                "test Name",
                LocalDate.now().minusDays(10));
    }

    public static List<User> getUsers(int count) {

        var listOfUsers = new ArrayList<User>();

        for (int i = 0; i < count; i++) {

            var j = i + 1;
            listOfUsers.add(new User(
                    null,
                    j + "test@google.com",
                    "test Login " + j,
                    "test Name " + j,
                    LocalDate.now().minusDays(10)));
        }
        return listOfUsers;
    }
}
