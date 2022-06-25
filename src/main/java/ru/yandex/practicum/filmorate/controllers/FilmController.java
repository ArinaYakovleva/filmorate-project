package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends BaseController<Film> {

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        validate(film);
        return add(film);
    }

    @PutMapping
    public Film editFilm(@Valid @RequestBody Film film) throws ValidationException {
        validate(film);
        return edit(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return getAll();
    }

    private void validate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-25")))
            throw new ValidationException("Дата релиза должна быть после 28 декабря 1895 года", String.valueOf(film.getReleaseDate()));
    }
}
