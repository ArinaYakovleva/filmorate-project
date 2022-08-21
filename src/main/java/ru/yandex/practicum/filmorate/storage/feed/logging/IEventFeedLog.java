package ru.yandex.practicum.filmorate.storage.feed.logging;


public interface IEventFeedLog {
    void log(Object result, String methodName, Object... args);
}
