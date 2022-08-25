package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.ILikeStorage;

@Service
public class LikeService extends ValidateService {

    private final ILikeStorage likeStorage;

    public LikeService(ILikeStorage likeStorage) {
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
