package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface IReviewController {
    @GetMapping
    List<Review> getAllReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false) Integer count);

    @PostMapping
    Review add(@RequestBody Review data);

    @PutMapping
    Review edit(@RequestBody Review data);

    @GetMapping("/{id}")
    Review getOne(@PathVariable Long id);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);

    @PutMapping("/{id}/like/{userId}")
    void like(@PathVariable Long id, @PathVariable Long userId);

    @PutMapping("/{id}/dislike/{userId}")
    void dislike(@PathVariable Long id, @PathVariable Long userId);

    @DeleteMapping("/{id}/like/{userId}")
    void deleteLike(@PathVariable Long id, @PathVariable Long userId);

    @DeleteMapping("/{id}/dislike/{userId}")
    void deleteDislike(@PathVariable Long id, @PathVariable Long userId);
}
