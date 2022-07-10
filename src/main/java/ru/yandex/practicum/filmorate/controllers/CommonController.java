package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface CommonController<T extends Model> {

    @PostMapping
    T add(@Valid @RequestBody T data);

    @PutMapping
    T edit(@Valid @RequestBody T data);

    @GetMapping
    List<T> getAll();

    @GetMapping("/{id}")
    T getOne(@PathVariable Long id);

}
