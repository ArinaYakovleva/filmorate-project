package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message, String param) {
        super(message);
        log.info("Ошибка запроса: {}. Переданное значение: {}", message, param);
    }

}
