package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {

    public ValidationException(String message, String param) {
        super(message);
        log.info("Ошибка валиадации: {}. Переданное значение: {}", message, param);
    }

}
