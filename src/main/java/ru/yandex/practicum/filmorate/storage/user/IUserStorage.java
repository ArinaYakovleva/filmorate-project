package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IBaseStorage;

import java.util.List;

public interface IUserStorage extends IBaseStorage<User> {

    List<User> getListCommonFriend(Long userId, Long otherId);

    List<User> getListFriend(Long userId);
}
