package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Genre extends Model {

    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;

    public Genre(Long id, String name) {
        super(id);
        this.name = name;
    }
}
