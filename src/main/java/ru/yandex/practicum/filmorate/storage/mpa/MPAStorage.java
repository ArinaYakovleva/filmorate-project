package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface MPAStorage {

    List<Rating> getAll();

    Rating getOne(Long id);
}
