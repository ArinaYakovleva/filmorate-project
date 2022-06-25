package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public abstract class BaseController<T extends Model> implements CommonController<T> {

    private final HashMap<Long, T> dataHashMap = new HashMap<>();
    private Long generatorId = 0L;

    @Override
    public T add(T data) throws ValidationException {
        generatorId++;
        data.setId(generatorId);
        baseValidate(data);
        validate(data);
        dataHashMap.put(generatorId, data);
        log.info("Добавлен объект {}, {}", data.getClass(), data);
        return data;
    }

    @Override
    public T edit(T data) throws ValidationException {
        baseValidate(data);
        validate(data);
        T oldElement = dataHashMap.put(data.getId(), data);
        log.info("Объект изменен. Старое значение - {}. Новое значение - {}", oldElement, data);
        return data;
    }

    @Override
    public ArrayList<T> getAll() {
        return new ArrayList<>(dataHashMap.values());
    }

    private void baseValidate(T data) throws ValidationException {
        if (data.getId() == null || data.getId() < 0)
            throw new ValidationException("Id должен быть положительным числом", String.valueOf(data.getId()));
    }

    abstract void validate(T data) throws ValidationException;

}
