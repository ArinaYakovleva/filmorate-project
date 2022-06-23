package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ValidationException extends Exception{

    public ValidationException(String message, String param){
        super(message);
        log.info("Ошибка валиадации - {}. Переданное значение - {}", message, param);
    }
}
