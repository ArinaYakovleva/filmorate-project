package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

public interface MPAStorage {

    List<MPARating> getAll();

    MPARating getOne(Long id);
}
