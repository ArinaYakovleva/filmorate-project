package ru.yandex.practicum.filmorate.testUtils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.time.LocalDate;
import java.util.ArrayList;

public class FilmGenerator {

    public static Film generateFilm() {
        return new Film.FilmBuilder(null)
                .withName("test name")
                .withDescription("test description")
                .withReleaseDate(LocalDate.now())
                .withDuration(120)
                .withRate(4L)
                .withMpa(new MPARating(1L, "G", "без ограничений"))
                .build();
    }

    public static ArrayList<Film> generateFilm(int count) {
        var films = new ArrayList<Film>();

        for (int i = 0; i < count; i++) {

            var j = i + 1;
            var film = new Film.FilmBuilder(null)
                    .withName("test name " + j)
                    .withDescription("test description " + j)
                    .withReleaseDate(LocalDate.now())
                    .withDuration(120 + j)
                    .withRate(4L + j)
                    .withMpa(new MPARating(1L, "G", "без ограничений"))
                    .build();
            films.add(film);
        }

        return films;
    }
}
