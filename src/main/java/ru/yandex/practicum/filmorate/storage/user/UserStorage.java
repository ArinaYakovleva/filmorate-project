package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

public interface UserStorage extends BaseStorage<User> {

    List<User> getListCommonFriend(Long userId, Long otherId);

    List<User> getListFriend(Long userId);
}
