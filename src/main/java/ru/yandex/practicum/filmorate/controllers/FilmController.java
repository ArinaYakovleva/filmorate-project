package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    HashMap<Long, Film> filmHashMap = new HashMap<>();
    private Long generatorId = 0L;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        generatorId++;
        film.setId(generatorId);
        validate(film);
        filmHashMap.put(generatorId,film);
        return film;
    }

    @PutMapping
    public Film editFilm(@Valid @RequestBody Film film) throws ValidationException {
        validate(film);
        filmHashMap.put(film.getId(),film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms(){
        return new ArrayList<>(filmHashMap.values());
    }

    private void validate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-25")))
            throw new ValidationException("Дата релиза должна быть после 28 декабря 1895 года", String.valueOf(film.getReleaseDate()));
        if (film.getId() == null || film.getId()<0)
            throw new ValidationException("Id должен быть положительным числом", String.valueOf(film.getId()));
    }
}
