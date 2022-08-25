package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface IReviewStorage {
    List<Review> getAll(Long filmId, Integer count);

    Review getOne(Long id);

    Review add(Review data);

    Review edit(Review data);

    void delete(Long reviewId);

    int like(Long reviewId, Long userId, boolean isLike);

    int deleteLike(Long reviewId, Long userId, boolean isLike);
}
