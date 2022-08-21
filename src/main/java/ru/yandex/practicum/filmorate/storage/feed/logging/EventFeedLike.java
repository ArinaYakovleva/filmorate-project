package ru.yandex.practicum.filmorate.storage.feed.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

@Component("EventFeedLike")
@Slf4j
public class EventFeedLike implements IEventFeedLog {

    private final EventFeedBase eventFeedBase;

    @Autowired
    public EventFeedLike(EventFeedBase eventFeedBase) {
        this.eventFeedBase = eventFeedBase;
    }

    @Override
    public void log(Object result, String methodName, Object... args) {
        switch (methodName) {
            case "addLike":
                eventFeedBase.execute((Long) args[1], EventType.LIKE, Operation.ADD, (Long) args[0]);
                break;
            case "deleteLike":
                eventFeedBase.execute((Long) args[1], EventType.LIKE, Operation.REMOVE, (Long) args[0]);
                break;
            default:
                log.warn("Метод для логирования действий пользователя не определен. Переданный метод - {}", methodName);
        }
    }
}
