package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.IBaseStorage;

import java.util.List;

public interface IDirectorStorage extends IBaseStorage<Director> {

    /**
     * @return Список всех режиссеров
     */
    List<Director> getAll();

    /**
     * @return режиссера по id
     */
    Director getOne(Long id);

    void remove(Long id);
}
