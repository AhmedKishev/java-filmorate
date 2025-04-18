package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDbStorage;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedDbStorage feedDbStorage;

    public void addEvent(long userId, FeedEvent.EventType type, FeedEvent.Operation operation, long entityId) {
        FeedEvent event = FeedEvent.builder().timestamp(System.currentTimeMillis()).userId(userId).eventType(type).operation(operation).entityId(entityId).build();
        feedDbStorage.addEvent(event);
    }

    public List<FeedEvent> getUserFeed(long userId) {
        return feedDbStorage.getUserFeed(userId);
    }
}