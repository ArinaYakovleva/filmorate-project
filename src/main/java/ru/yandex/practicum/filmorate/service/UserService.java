package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService extends BaseService<User, UserStorage> {

    @Autowired
    public UserService(@Qualifier("userStorageDB") UserStorage storage) {
        super(storage);
    }

    @Override
    protected void validate(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
    }

    public void addFriend(Long userId, Long friendId) {
        baseValidate(userId);
        baseValidate(friendId);
        storage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        baseValidate(userId);
        baseValidate(friendId);
        storage.deleteFriend(userId, friendId);
    }

    public List<User> getListCommonFriend(Long userId, Long otherId) {
        baseValidate(userId);
        baseValidate(otherId);
        return storage.getListCommonFriend(userId, otherId);
    }

    public List<User> getListFriend(Long userId) {
        baseValidate(userId);
        return storage.getListFriend(userId);
    }

}
