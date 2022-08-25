package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.IReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController implements IReviewController {
    private final IReviewService service;

    @Autowired
    public ReviewController(IReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Review> getAllReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return service.getAllReviews(filmId, count);

    }

    @Override
    @PostMapping
    public Review add(@Valid Review data) {
        return service.add(data);
    }

    @Override
    @PutMapping
    public Review edit(@Valid Review data) {
        return service.edit(data);
    }

    @Override
    @GetMapping("/{id}")
    public Review getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Override
    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        service.like(id, userId, true);
    }

    @Override
    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable Long id, @PathVariable Long userId) {
        service.like(id, userId, false);
    }

    @Override
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId, true);
    }

    @Override
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId, false);
    }
}
