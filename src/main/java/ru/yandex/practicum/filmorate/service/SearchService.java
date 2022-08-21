package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.IFilmStorage;

import java.util.List;

@Service
public class SearchService {

    private final IFilmStorage filmStorage;

    public SearchService(IFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> find(String query, List<String> by) {
        String directorQuery = by.contains("director") ? query.toLowerCase() : null;
        String titleQuery = by.contains("title") ? query.toLowerCase() : null;
        return filmStorage.getFilmsBySearch(directorQuery, titleQuery);
    }
}
