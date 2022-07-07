package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Model;

import javax.validation.Valid;
import java.util.List;

public interface BaseStorage<T extends Model> {

    T add(@Valid T data);

    T edit(@Valid T data);

    List<T> getAll();

    T getOne(Long id);

    Long generateId();
}
