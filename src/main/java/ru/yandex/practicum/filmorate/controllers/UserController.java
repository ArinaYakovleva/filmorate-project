package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {

    @Override
    void validate(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
    }

}
