package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.feed.EventFeed;
import ru.yandex.practicum.filmorate.service.EventFeedService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class EventFeedController {

    private final EventFeedService eventFeedService;

    @Autowired
    public EventFeedController(EventFeedService eventFeedService) {
        this.eventFeedService = eventFeedService;
    }

    @GetMapping("/{id}/feed")
    public List<EventFeed> getListEventFeed(@PathVariable Long id) {
        return eventFeedService.getList(id);
    }

}
