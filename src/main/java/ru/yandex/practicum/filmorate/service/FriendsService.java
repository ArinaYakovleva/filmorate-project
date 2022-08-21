package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.friends.IFriendsStorage;

@Service
public class FriendsService extends ValidateService {

    private final IFriendsStorage friendsStorage;

    @Autowired
    public FriendsService(IFriendsStorage friendsStorage) {
        this.friendsStorage = friendsStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        validateId(userId, friendId);
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        validateId(userId, friendId);
        friendsStorage.deleteFriend(userId, friendId);
    }

}
