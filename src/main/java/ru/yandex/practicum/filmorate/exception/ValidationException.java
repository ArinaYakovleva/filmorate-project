package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class ValidationException extends RuntimeException {

    public ValidationException(String message, String param) {
        super(message);
        log.info("Ошибка валиадации: {}. Переданное значение: {}", message, param);
    }

}
