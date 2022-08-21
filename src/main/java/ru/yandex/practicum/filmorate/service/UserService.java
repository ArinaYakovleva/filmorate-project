package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.IUserStorage;

import java.util.List;

@Service
public class UserService extends BaseService<User, IUserStorage> {

    @Autowired
    public UserService(@Qualifier("userStorageDB") IUserStorage storage) {
        super(storage);
    }

    @Override
    protected void validate(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
    }

    public List<User> getListCommonFriend(Long userId, Long otherId) {
        validateId(userId, otherId);
        return storage.getListCommonFriend(userId, otherId);
    }

    public List<User> getListFriend(Long userId) {
        validateId(userId);
        return storage.getListFriend(userId);
    }
}
