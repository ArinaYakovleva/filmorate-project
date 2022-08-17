package ru.yandex.practicum.filmorate.testUtils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.time.LocalDate;
import java.util.ArrayList;

public class FilmGenerator {

    public static Film generateFilm() {
        return new Film(null,
                "test name",
                "test description",
                LocalDate.now(),
                120,
                4L,
                new MPARating(1L, "G", "без ограничений"));
    }

    public static ArrayList<Film> generateFilm(int count) {
        var films = new ArrayList<Film>();

        for (int i = 0; i < count; i++) {

            var j = i + 1;
            var film = new Film(null,
                    "test name " + j,
                    "test description " + j,
                    LocalDate.now(),
                    120,
                    4L + j,
                    new MPARating(1L, "G", "без ограничений"));
            films.add(film);
        }

        return films;
    }
}
