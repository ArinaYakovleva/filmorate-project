package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface IGenreStorage {

    List<Genre> getAll();

    Genre getOne(Long id);
}
