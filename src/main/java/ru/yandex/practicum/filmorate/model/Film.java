package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends Model {

    @NotBlank(message = "Наименование не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Длительность должна быть больше нуля")
    private int duration;

    public Film(Long id,
                @NotBlank(message = "Наименование не может быть пустым") String name,
                @Size(max = 200, message = "Описание не должно превышать 200 символов") String description,
                LocalDate releaseDate,
                @Positive(message = "Длительность должна быть больше нуля") int duration) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
