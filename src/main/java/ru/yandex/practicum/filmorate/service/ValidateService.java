package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.BadRequestException;

import java.util.Arrays;


public abstract class ValidateService {

    protected void validateId(Long... id) {
        Arrays.stream(id).forEach((item) -> {
            if (item == null || item < 0)
                throw new BadRequestException("Id должен быть положительным числом", String.valueOf(item));
        });
    }

}
