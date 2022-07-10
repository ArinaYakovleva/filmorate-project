package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface UserStorage extends BaseStorage<User> {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getListCommonFriend(Long userId, Long otherId);

    List<User> getListFriend(Long userId);
}
