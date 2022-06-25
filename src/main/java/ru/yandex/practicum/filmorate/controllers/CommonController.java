package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Model;

import javax.validation.Valid;
import java.util.List;

public interface CommonController <T extends Model> {

    @PostMapping
    T add(@Valid @RequestBody T data) throws ValidationException;

    @PutMapping
    T edit(@Valid @RequestBody T data) throws ValidationException;

    @GetMapping
    List<T> getAll();
}
