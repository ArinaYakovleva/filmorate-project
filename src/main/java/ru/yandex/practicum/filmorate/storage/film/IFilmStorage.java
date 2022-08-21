package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.IBaseStorage;

import java.util.List;

public interface IFilmStorage extends IBaseStorage<Film> {

    List<Film> getPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> getDirectorFilms(Long directorId, String sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
