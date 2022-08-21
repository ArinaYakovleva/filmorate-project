package ru.yandex.practicum.filmorate.controllers;

import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.service.CommonService;

import java.util.List;

public abstract class BaseController<T extends Model, S extends CommonService<T>> implements CommonController<T> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @Override
    public T add(T data) {
        return service.add(data);
    }

    @Override
    public T edit(T data) {
        return service.edit(data);
    }

    @Override
    public List<T> getAll() {
        return service.getAll();
    }

    @Override
    public T getOne(Long id) {
        return service.getOne(id);
    }

    @Override
    public void remove(Long id) {
        service.remove(id);
    }
}
