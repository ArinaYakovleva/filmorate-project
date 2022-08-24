package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class Review {
    @NotNull
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Long userId;
    @NotNull
    private Long filmId;
    private Long reviewId;
    private Long useful;
}
