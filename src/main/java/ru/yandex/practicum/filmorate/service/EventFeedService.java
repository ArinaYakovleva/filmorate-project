package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.feed.EventFeed;
import ru.yandex.practicum.filmorate.storage.feed.EventFeedStorage;

import java.util.List;

@Service
public class EventFeedService extends ValidateService {

    private final EventFeedStorage eventFeedStorage;

    public EventFeedService(EventFeedStorage eventFeedStorage) {
        this.eventFeedStorage = eventFeedStorage;
    }

    public List<EventFeed> getList(Long userId) {
        validateId(userId);
        return eventFeedStorage.getAll(userId);
    }

}
