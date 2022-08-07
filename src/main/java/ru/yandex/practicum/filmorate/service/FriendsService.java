package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;

@Service
public class FriendsService extends ValidateService {

    private final FriendsStorage friendsStorage;

    @Autowired
    public FriendsService(FriendsStorage friendsStorage) {
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
