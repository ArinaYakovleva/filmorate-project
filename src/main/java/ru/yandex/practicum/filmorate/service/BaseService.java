package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.storage.IBaseStorage;

import java.util.List;

public abstract class BaseService<T extends Model, R extends IBaseStorage<T>> extends ValidateService implements ICommonService<T> {

    protected final R storage;

    protected BaseService(R storage) {
        this.storage = storage;
    }

    @Override
    public T add(T data) {
        validate(data);
        return storage.add(data);
    }

    @Override
    public T edit(T data) {
        validate(data);
        validateId(data.getId());
        return storage.edit(data);
    }

    @Override
    public List<T> getAll() {
        return storage.getAll();
    }

    @Override
    public T getOne(Long id) throws ValidationException {
        validateId(id);
        return storage.getOne(id);
    }

    @Override
    public void remove(Long id) throws ValidationException {
        validateId(id);
        storage.remove(id);
    }

    abstract protected void validate(T data);

}
