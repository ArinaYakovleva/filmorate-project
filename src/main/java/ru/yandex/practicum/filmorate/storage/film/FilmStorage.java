package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

public interface FilmStorage extends BaseStorage<Film> {

    List<Film> getPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> getDirectorFilms(Long directorId, String sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
