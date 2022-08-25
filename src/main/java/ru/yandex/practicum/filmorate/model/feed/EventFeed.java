package ru.yandex.practicum.filmorate.model.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFeed {

    private Long eventId;
    @NonNull
    private Long userId;
    @NonNull
    private Long entityId;
    private EventType eventType;
    private Operation operation;
    private Long timestamp;

}
