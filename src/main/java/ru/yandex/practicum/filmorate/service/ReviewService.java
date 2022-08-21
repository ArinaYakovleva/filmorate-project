package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.IReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService extends ValidateService implements IReviewService {
    private final IReviewStorage storage;

    @Autowired
    public ReviewService(IReviewStorage storage) {
        this.storage = storage;
    }

    public List<Review> getAllReviews(Long filmId, Integer count) {
        return storage.getAll(filmId, count);
    }

    public void delete(Long id) {
        storage.delete(id);
    }

    @Override
    public Review add(Review data) {
        validateId(data.getFilmId(), data.getUserId());
        Review review = storage.add(data);
        if (review == null) {
            String errorMessage = String.format("Не найдена запись с reviewId=%d, filmId=%d",
                    data.getReviewId(), data.getFilmId());
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.info("Добавлен отзыв: " + review);
        return review;
    }

    @Override
    public Review edit(Review data) {
        validateId(data.getFilmId(), data.getUserId());
        Review review = storage.edit(data);
        if (review == null) {
            String errorMessage = String.format("Не найдена запись с reviewId=%d, filmId=%d",
                    data.getReviewId(), data.getFilmId());
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.info(String.format("Обновлен отзыв с ID=%d: %s", data.getReviewId(), review));
        return review;
    }

    @Override
    public Review getOne(Long id) {
        Review review = storage.getOne(id);
        if (review == null) {
            throw getNotFoundError(id);
        }
        return review;
    }

    @Override
    public void like(Long reviewId, Long userId, boolean isLike) {
        int result;
        try {
            result = storage.like(reviewId, userId, isLike);
        } catch (DataIntegrityViolationException e) {
            String params = String.format("reviewId=%d, userId=%d", reviewId, userId);
            String errorMessage = "Ошибка лайка отзыва " + params;
            log.error(errorMessage);
            throw new BadRequestException(errorMessage, params);
        }
        if (result == 0) {
            throw new NotFoundException(String.format("Не найден отзыв с reviewId=%d, userId=%d", reviewId, userId));
        }
        log.info(String.format("Пользователь с ID=%d поставил лайк отзыву с ID=%d", userId, reviewId));
    }

    @Override
    public void deleteLike(Long reviewId, Long userId, boolean isLike) {
        int result = storage.deleteLike(reviewId, userId, isLike);
        if (result == 0) {
            throw new NotFoundException(String.format("Не найден отзыв с reviewId=%d, userId=%d", reviewId, userId));
        }
        log.info(String.format("Пользователь с ID=%d удалил лайк у отзыва с ID=%d", userId, reviewId));
    }

    private BadRequestException getNotFoundError(Long id) {
        String errorMessage = String.format("Отзыв с ID %d не найден", id);
        throw new NotFoundException(errorMessage);
    }
}
