package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Model;

import javax.validation.Valid;
import java.util.List;

public interface ICommonController<T extends Model> {

    @PostMapping
    T add(@Valid @RequestBody T data);

    @PutMapping
    T edit(@Valid @RequestBody T data);

    @GetMapping
    List<T> getAll();

    @GetMapping("/{id}")
    T getOne(@PathVariable Long id);

    @DeleteMapping("/{id}")
    void remove(@PathVariable Long id);
}
