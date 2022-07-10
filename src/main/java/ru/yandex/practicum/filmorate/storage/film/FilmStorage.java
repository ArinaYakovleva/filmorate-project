package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

public interface FilmStorage extends BaseStorage<Film> {

    void addLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> getPopularFilms(Integer count);

}
