package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

public abstract class BaseService<T extends Model, R extends BaseStorage<T>> implements CommonService<T> {

    protected final R storage;

    protected BaseService(R storage) {
        this.storage = storage;
    }

    @Override
    public T add(T data) {
        validate(data);
        data.setId(storage.generateId());
        baseValidate(data);
        return storage.add(data);
    }

    @Override
    public T edit(T data) {
        validate(data);
        baseValidate(data);
        return storage.edit(data);
    }

    @Override
    public List<T> getAll() {
        return storage.getAll();
    }

    @Override
    public T getOne(Long id) throws ValidationException {
        baseValidate(id);
        return storage.getOne(id);
    }

    private void baseValidate(T data) {
        if (data.getId() == null || data.getId() < 0)
            throw new BadRequestException("Id должен быть положительным числом", String.valueOf(data.getId()));
    }

    protected void baseValidate(Long id) {
        if (id == null || id < 0)
            throw new BadRequestException("Id должен быть положительным числом", String.valueOf(id));
    }

    abstract protected void validate(T data);

}
