package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.IFilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService extends BaseService<Film, IFilmStorage> {

    @Autowired
    public FilmService(@Qualifier("filmStorageDB") IFilmStorage storage) {
        super(storage);
    }

    @Override
    protected void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-25")))
            throw new ValidationException("Дата релиза должна быть после 28 декабря 1895 года", String.valueOf(film.getReleaseDate()));
    }

    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        return storage.getPopularFilms(count, genreId, year);
    }

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        return storage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }
}
