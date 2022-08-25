package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Режиссер фильма
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Director extends Model {
    // Имя режиссера
    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;

    public Director(Long id, String name) {
        super(id);
        this.name = name;
    }
}
