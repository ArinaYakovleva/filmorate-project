package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends Model {

    @NotBlank(message = "Наименование фильма не может быть пустым")
    private String name;

    @NonNull
    private Long rate = 0L;
    @NonNull
    private MPARating mpa;
    private Set<Director> directors = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();

    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма должна быть больше нуля")
    private int duration;

    public Film(Long id,
                @NotBlank(message = "Наименование фильма не может быть пустым") String name,
                @Size(max = 200, message = "Описание фильма не должно превышать 200 символов") String description,
                LocalDate releaseDate,
                @Positive(message = "Длительность фильма должна быть больше нуля") int duration) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id,
                @NotBlank(message = "Наименование фильма не может быть пустым") String name,
                @Size(max = 200, message = "Описание фильма не должно превышать 200 символов") String description,
                LocalDate releaseDate,
                @Positive(message = "Длительность фильма должна быть больше нуля") int duration,
                Long rate, MPARating mpa, Set<Genre> genres) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (rate != null) this.rate = rate;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(Long id,
                @NotBlank(message = "Наименование фильма не может быть пустым") String name,
                @Size(max = 200, message = "Описание фильма не должно превышать 200 символов") String description,
                LocalDate releaseDate,
                @Positive(message = "Длительность фильма должна быть больше нуля") int duration,
                Long rate, MPARating mpa) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
    }

    public Film() {
    }

}
