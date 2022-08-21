package ru.yandex.practicum.filmorate.storage.friends;


public interface IFriendsStorage {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

}
