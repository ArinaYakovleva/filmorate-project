package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;

@Service
public class MPAService {

    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Rating> getAll() {
        return mpaStorage.getAll();
    }

    public Rating getOne(Long id) {
        baseValidate(id);
        return mpaStorage.getOne(id);
    }

    private void baseValidate(Long id) {
        if (id == null || id < 0)
            throw new BadRequestException("Id должен быть положительным числом", String.valueOf(id));
    }

}
