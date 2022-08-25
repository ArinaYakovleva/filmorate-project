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

    public Film() {
    }

    public Film(Long id) {
        super(id);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FilmBuilder extends Model {

        private Film newFilm;

        public FilmBuilder(Long id) {
            newFilm = new Film(id);
        }

        public FilmBuilder withName(String name) {
            newFilm.name = name;
            return this;
        }

        public FilmBuilder withRate(Long rate) {
            newFilm.rate = rate;
            return this;
        }

        public FilmBuilder withMpa(MPARating mpa) {
            newFilm.mpa = mpa;
            return this;
        }

        public FilmBuilder withDirectors(Set<Director> directors) {
            newFilm.directors = directors;
            return this;
        }

        public FilmBuilder withGenres(Set<Genre> genres) {
            newFilm.genres = genres;
            return this;
        }

        public FilmBuilder withDescription(String description) {
            newFilm.description = description;
            return this;
        }

        public FilmBuilder withReleaseDate(LocalDate releaseDate) {
            newFilm.releaseDate = releaseDate;
            return this;
        }

        public FilmBuilder withDuration(int duration) {
            newFilm.duration = duration;
            return this;
        }

        public Film build() {
            return newFilm;
        }
    }
}
