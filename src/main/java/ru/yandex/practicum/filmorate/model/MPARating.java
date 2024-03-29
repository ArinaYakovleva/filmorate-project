package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class MPARating extends Model {

    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;
    private String description;

    public MPARating(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }
}
