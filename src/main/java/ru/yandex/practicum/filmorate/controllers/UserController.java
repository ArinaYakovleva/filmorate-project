package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    HashMap<Long, User> userHashMap = new HashMap<>();
    private Long generatorId = 0L;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        generatorId++;
        user.setId(generatorId);
        validate(user);
        userHashMap.put(generatorId,user);
        return user;
    }

    @PutMapping
    public User editUser(@Valid @RequestBody User user) throws ValidationException {
        validate(user);
        userHashMap.put(user.getId(),user);
        return user;
    }

    @GetMapping
    public List<User> getAllUser(){
        return new ArrayList<>(userHashMap.values());
    }

    private void validate(User user) throws ValidationException {
        if (user.getId() == null || user.getId()<0)
            throw new ValidationException("Id должен быть положительным числом", String.valueOf(user.getId()));
        if (user.getName().isBlank()) user.setName(user.getLogin());
    }

}
