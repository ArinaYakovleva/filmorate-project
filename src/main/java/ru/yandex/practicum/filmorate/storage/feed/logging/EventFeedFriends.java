package ru.yandex.practicum.filmorate.storage.feed.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;


@Component("EventFeedFriends")
@Slf4j
public class EventFeedFriends implements IEventFeedLog {

    private final EventFeedBase eventFeedBase;

    public EventFeedFriends(EventFeedBase eventFeedBase) {
        this.eventFeedBase = eventFeedBase;
    }

    @Override
    public void log(Object result, String methodName, Object... args) {
        switch (methodName) {
            case "addFriend":
                eventFeedBase.execute((Long) args[0], EventType.FRIEND, Operation.ADD, (Long) args[1]);
                break;
            case "deleteFriend":
                eventFeedBase.execute((Long) args[0], EventType.FRIEND, Operation.REMOVE, (Long) args[1]);
                break;
            default:
                log.warn("Метод для логирования действий пользователя не определен. Переданный метод - {}", methodName);
        }
    }
}
