package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public abstract class BaseController<T extends Model>{

    private final HashMap<Long, T> filmHashMap = new HashMap<>();
    private Long generatorId = 0L;

    protected T add(T element) throws ValidationException {
        generatorId++;
        element.setId(generatorId);
        baseValidate(element);
        filmHashMap.put(generatorId, element);
        log.info("Добавлен объект {}, {}", element.getClass(),element);
        return element;
    }

    protected T edit(T element) throws ValidationException {
        baseValidate(element);
        T oldElement = filmHashMap.put(element.getId(), element);
        log.info("Объект изменен. Старое значение - {}. Новое значение - {}", oldElement, element);
        return element;
    }

    protected ArrayList<T> getAll(){
        return new ArrayList<>(filmHashMap.values());
    }

    private void baseValidate(T element) throws ValidationException {
        if (element.getId() == null || element.getId() < 0)
            throw new ValidationException("Id должен быть положительным числом", String.valueOf(element.getId()));
    }
}
