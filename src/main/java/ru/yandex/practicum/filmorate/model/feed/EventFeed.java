package ru.yandex.practicum.filmorate.model.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFeed {

    Long eventId;
    @NonNull
    Long userId;
    @NonNull
    Long entityId;
    EventType eventType;
    Operation operation;
    Long timestamp;

}
