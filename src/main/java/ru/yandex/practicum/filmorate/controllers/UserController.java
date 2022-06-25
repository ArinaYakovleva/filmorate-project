package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends BaseController<User> {

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        validate(user);
        return add(user);
    }

    @PutMapping
    public User editUser(@Valid @RequestBody User user) throws ValidationException {
        validate(user);
        return edit(user);
    }

    @GetMapping
    public List<User> getAllUser() {
        return getAll();
    }

    private void validate(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
    }
}
