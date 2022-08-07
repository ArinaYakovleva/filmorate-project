package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
public class LikeService extends ValidateService {

    private final LikeStorage likeStorage;

    public LikeService(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    public void addLike(Long id, Long userId) {
        validateId(id, userId);
        likeStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        validateId(id, userId);
        likeStorage.deleteLike(id, userId);
    }

}
