package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface IReviewService {
    List<Review> getAllReviews(Long filmId, Integer count);

    void delete(Long id);

    Review add(Review data);

    Review edit(Review data);

    Review getOne(Long id);

    void like(Long reviewId, Long userId, boolean isLike);

    void deleteLike(Long reviewId, Long userId, boolean isLike);

}
