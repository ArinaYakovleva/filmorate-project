package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Model;

import javax.validation.Valid;
import java.util.List;

public interface ICommonService<T extends Model> {

    T add(@Valid T data);

    T edit(@Valid T data);

    List<T> getAll();

    T getOne(Long id);

    void remove(Long id);
}
