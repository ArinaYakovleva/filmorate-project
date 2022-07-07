package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ImMemoryBaseStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage extends ImMemoryBaseStorage<User> implements UserStorage {

    private final HashMap<Long, Set<User>> friendsHaspMap = new HashMap<>();

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = dataHashMap.get(userId);
        User friend = dataHashMap.get(friendId);
        Set<User> userSet = friendsHaspMap.getOrDefault(userId, new HashSet<>());
        userSet.add(friend);
        friendsHaspMap.put(userId, userSet);
        Set<User> friendSet = friendsHaspMap.getOrDefault(friendId, new HashSet<>());
        friendSet.add(user);
        friendsHaspMap.put(friendId, friendSet);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = dataHashMap.get(userId);
        User friend = dataHashMap.get(friendId);
        Set<User> userSet = friendsHaspMap.get(userId);
        userSet.remove(friend);
        friendsHaspMap.put(userId, userSet);
        Set<User> friendSet = friendsHaspMap.get(friendId);
        friendSet.remove(user);
        friendsHaspMap.put(friendId, friendSet);
    }

    @Override
    public List<User> getListCommonFriend(Long userId, Long otherId) {
        Set<User> userSet = friendsHaspMap.getOrDefault(userId, new HashSet<>());
        Set<User> friendSet = friendsHaspMap.getOrDefault(otherId, new HashSet<>());
        return userSet.stream().filter(friendSet::contains).collect(Collectors.toList());
    }

    @Override
    public List<User> getListFriend(Long userId) {
        return new ArrayList<>(friendsHaspMap.getOrDefault(userId, new HashSet<>()));
    }

}
