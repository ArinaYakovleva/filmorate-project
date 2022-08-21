package ru.yandex.practicum.filmorate.storage.recommendation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface IRecommendationStorage {
    List<Film> getRecommendations(Long userId);
}
