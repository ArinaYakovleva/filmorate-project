package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.IRecommendationStorage;

import java.util.List;

@Service
public class RecommendationService extends ValidateService {

    private final IRecommendationStorage recommendationStorage;

    public RecommendationService(IRecommendationStorage recommendationStorage) {
        this.recommendationStorage = recommendationStorage;
    }

    public List<Film> getRecommendations(Long userId) {
        validateId(userId);
        return recommendationStorage.getRecommendations(userId);
    }
}
