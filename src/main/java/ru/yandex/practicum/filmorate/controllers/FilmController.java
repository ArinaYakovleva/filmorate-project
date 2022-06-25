package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {

    @Override
    void validate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-25")))
            throw new ValidationException("Дата релиза должна быть после 28 декабря 1895 года", String.valueOf(film.getReleaseDate()));
    }

}
