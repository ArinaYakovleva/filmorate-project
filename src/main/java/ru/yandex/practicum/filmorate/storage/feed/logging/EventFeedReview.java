package ru.yandex.practicum.filmorate.storage.feed.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

@Component("EventFeedReview")
@Slf4j
public class EventFeedReview implements IEventFeedLog, IEventFeedAroundLog {

    private final EventFeedBase eventFeedBase;

    @Autowired
    public EventFeedReview(EventFeedBase eventFeedBase) {
        this.eventFeedBase = eventFeedBase;
    }

    public void delete(Long reviewId) {
        Long userId = eventFeedBase.getId("SELECT user_id FROM REVIEWS WHERE REVIEW_ID=?", "user_id", reviewId);
        eventFeedBase.execute(userId, EventType.REVIEW, Operation.REMOVE, reviewId);
    }

    @Override
    public void log(Object result, String methodName, Object... args) {
        Review review = (Review) result;
        switch (methodName) {
            case "add":
                eventFeedBase.execute(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
                break;
            case "edit":
                eventFeedBase.execute(review.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());
                break;
            case "delete":
                Object[] mapArgs = (Object[]) args[0];
                eventFeedBase.execute((Long) args[1], EventType.REVIEW, Operation.REMOVE, (Long) mapArgs[0]);
                break;
            default:
                log.warn("Метод для логирования действий пользователя не определен. Переданный метод - {}", methodName);
        }
    }

    @Override
    public Object getResult(Object... args) {
        Long reviewId = (Long) args[0];
        return eventFeedBase.getId("SELECT user_id FROM REVIEWS WHERE REVIEW_ID=?", "user_id", reviewId);
    }
}
