package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
public class Review {
    private Long reviewId;
    @NotNull
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long filmId;
    private Long useful;
}
